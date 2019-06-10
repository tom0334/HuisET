import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.PersonRecAdapter
import com.tobo.huiset.gui.adapters.ProductRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ItemClickSupport
import io.realm.Sort
import com.google.android.material.snackbar.Snackbar
import com.tobo.huiset.utils.extensions.executeSafe
import com.tobo.huiset.utils.extensions.findAllCurrentProducts


class FragmentPurchases : HuisEtFragment() {


    var pickedPersonId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_purchases, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfileRec(view)
        initProducsRec(view)
    }

    private fun initProfileRec(view: View) {
        val pickUserRec = view.findViewById<RecyclerView>(R.id.pickUserRec)

        val profiles = realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("show", true)
            .sort("balance", Sort.ASCENDING)
            .findAll()

        pickUserRec.adapter = PersonRecAdapter(context!!, profiles, realm, true)
        pickUserRec.layoutManager = LinearLayoutManager(context!!)

        ItemClickSupport.addTo(pickUserRec).setOnItemClickListener { recyclerView, position, v ->
            setPersonAndUpdate(profiles.get(position)?.id)
        }
    }

    private fun initProducsRec(view: View) {
        val pickProductsRec = view.findViewById<RecyclerView>(R.id.pickProductsRec)
        pickProductsRec.visibility = View.VISIBLE

        val products = realm.findAllCurrentProducts()

        // this sets up the recyclerview to show the persons
        pickProductsRec.adapter = ProductRecAdapter(this.context!!, products, realm, true)
        pickProductsRec.layoutManager = LinearLayoutManager(this.context)

        ItemClickSupport.addTo(pickProductsRec).setOnItemClickListener { recyclerView, position, v ->

            val person = realm.where(Person::class.java)
                .equalTo("deleted", false)
                .equalTo("id", pickedPersonId)
                .findFirst() ?: return@setOnItemClickListener
            val product = products!!.get(position) ?: return@setOnItemClickListener

            var doneTransaction: Transaction? = null
            realm.executeTransaction {
                val trans = Transaction.create(person, product, true)
                person.addTransaction(trans, realm)
                doneTransaction = realm.copyToRealmOrUpdate(trans)


            }
            setPersonAndUpdate(null)

            Snackbar
                .make(view, "${product.name} gekocht door ${person.name}", 4000)
                .setAction("Undo") {
                    realm.executeSafe {
                        person.undoTransaction(doneTransaction, realm)
                        doneTransaction?.deleteFromRealm()
                    }
                }.show()
        }

    }


    fun setPersonAndUpdate(newPickedId: String?) {
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