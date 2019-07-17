import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.adapters.PurchasePersonRecAdapter
import com.tobo.huiset.gui.adapters.PurchaseProductRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ItemClickSupport
import io.realm.Sort


class FragmentPurchases : HuisEtFragment() {


    private var pickedPersonId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_purchases, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfileRec(view)
        initProductsRec(view)
    }

    override fun onTabReactivated(){
        // this resets the chosen person to none, putting you back to the pick person screen
        setPersonAndUpdate(null)
    }

    override fun onBackButtonPressed(): Boolean {
        //if we are already on the profiles screen, the activity must handle the back button
        if(pickedPersonId == null) return false
        //else we will handle it here
        setPersonAndUpdate(null)
        return true
    }

    private fun initProfileRec(view: View) {
        val pickUserRec = view.findViewById<RecyclerView>(R.id.pickUserRec)
        pickUserRec.addItemDecoration(DividerItemDecoration(pickUserRec.context, DividerItemDecoration.VERTICAL))


        val profiles = realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("show", true)
            .sort("row", Sort.ASCENDING)
            .findAll()

        pickUserRec.adapter = PurchasePersonRecAdapter(context!!, realm, profiles, true)
        pickUserRec.layoutManager = LinearLayoutManager(context!!)

        ItemClickSupport.addTo(pickUserRec).setOnItemClickListener { _, position, _ ->
            setPersonAndUpdate(profiles[position]?.id)
        }
    }

    private fun initProductsRec(view: View) {
        val pickProductsRec = view.findViewById<RecyclerView>(R.id.pickProductsRec)
        pickProductsRec.visibility = View.VISIBLE
        pickProductsRec.addItemDecoration(DividerItemDecoration(pickProductsRec.context, DividerItemDecoration.VERTICAL))


        val products = db.findAllCurrentProducts(excludeHidden = true)

        // this sets up the recyclerview to show the persons
        pickProductsRec.adapter = PurchaseProductRecAdapter(this.context!!, realm, products, true)
        pickProductsRec.layoutManager = LinearLayoutManager(this.context)

        ItemClickSupport.addTo(pickProductsRec).setOnItemClickListener { _, position, _ ->

            val person = realm.where(Person::class.java)
                .equalTo("deleted", false)
                .equalTo("id", pickedPersonId)
                .findFirst() ?: return@setOnItemClickListener
            val product = products!![position] ?: return@setOnItemClickListener

            var doneTransaction: Transaction? = null
            realm.executeTransaction {
                val trans = Transaction.create(person, product, true)
                person.addTransaction(trans)
                doneTransaction = realm.copyToRealmOrUpdate(trans)


            }
            setPersonAndUpdate(null)

            val snackbar = Snackbar
                .make(view, "${product.name} gekocht door ${person.name}", 4000)
                .setAction("Undo") {
                    db.undoTransaction(doneTransaction,person)
                }


            val marginBottom = (this.activity as HuisEtActivity).getSnackbarBottomMargin()
            val layoutParams = snackbar.view.layoutParams as FrameLayout.LayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                layoutParams.topMargin,
                layoutParams.rightMargin,
                layoutParams.bottomMargin + marginBottom
            )
            snackbar.show()
        }
    }


    private fun setPersonAndUpdate(newPickedId: String?) {
        this.pickedPersonId = newPickedId
        val view = this.view ?: return

        val userLayout = view.findViewById<View>(R.id.pickUserLayout)
        val productLayout = view.findViewById<View>(R.id.pickProductLayout)

        if (pickedPersonId == null) {
            userLayout.visibility = View.VISIBLE
            productLayout.visibility = View.GONE
        } else {
            val person = realm.where(Person::class.java)
                .equalTo("deleted", false)
                .equalTo("id", pickedPersonId)
                .findFirst()
            val boughtWhatTv = view.findViewById<TextView>(R.id.whatHaveYouBoughtText)
            boughtWhatTv.text = "${person!!.name}, Wat heb je gekocht?"
            userLayout.visibility = View.GONE
            productLayout.visibility = View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        setPersonAndUpdate(null)
    }


}