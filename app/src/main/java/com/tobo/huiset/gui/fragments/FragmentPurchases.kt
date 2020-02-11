import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tobo.huiset.R
import com.tobo.huiset.achievements.AchievementManager
import com.tobo.huiset.extendables.CelebratingHuisEtActivity
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.activities.PREFS_DEPOSIT_ID
import com.tobo.huiset.gui.activities.PREFS_HUISREKENING_ID
import com.tobo.huiset.gui.activities.EditProductActivity
import com.tobo.huiset.gui.adapters.PurchasePersonRecAdapter
import com.tobo.huiset.gui.adapters.PurchaseProductRecAdapter
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.utils.extensions.toCurrencyString


class FragmentPurchases : HuisEtFragment() {


    val depositEnabled: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFS_DEPOSIT_ID, false)
    val huisRekeningEnabled: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFS_HUISREKENING_ID, false)


    private var pickedPersonId: String? = null

    private var totalPurchasePrice: Int = 0
        set(value) {
            field = value
            view?.findViewById<TextView>(R.id.purchaseMoneyCounter)?.text = "Totaal: ${value.toCurrencyString()}"
        }

    private var depositPrice: Int = 0
        set(value) {
            field = value
            view?.findViewById<TextView>(R.id.depositMoneyCounter)?.text =
                if (depositEnabled)
                    " (+ ${value.toCurrencyString()})"
                else
                    ""
        }

    private val prodRecAdapter
        get() = view!!.findViewById<RecyclerView>(R.id.pickProductsRec).adapter as PurchaseProductRecAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_purchases, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProfileRec(view)
        initProductsRec(view)
    }

    override fun onTabReactivated(userTapped:Boolean){
        if(userTapped){
            reset()
        }
    }

    override fun onBackButtonPressed(): Boolean {
        //if we are already on the profiles screen, the activity must handle the back button
        if (pickedPersonId == null) return false
        //else we will handle it here
        reset()

        return true
    }

    private fun initProfileRec(view: View) {
        val pickUserRec = view.findViewById<RecyclerView>(R.id.pickUserRec)
        pickUserRec.addItemDecoration(DividerItemDecoration(pickUserRec.context, DividerItemDecoration.VERTICAL))

        val profiles = db.findAllCurrentPersons(true)

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


        val products = db.findAllCurrentProducts(Product.ONLY_BUYABLE)

        // this sets up the recyclerview to show the persons
        pickProductsRec.adapter = PurchaseProductRecAdapter(this, realm, products, true)
        pickProductsRec.layoutManager = LinearLayoutManager(this.context)

        view.findViewById<MaterialButton>(R.id.purchaseSaveButton).setOnClickListener {
            val person = db.getPersonWithId(pickedPersonId)!!
            var anythingBought = false
            products.forEach {
                val amount = prodRecAdapter.getFromMap(it.id)
                if (amount > 0) {
                    db.createAndSaveTransaction(person, it, amount, true, depositEnabled, huisRekeningEnabled)
                    anythingBought = true
                }
            }
            if (!anythingBought) {
                Toast.makeText(context, "Geen producten geselecteerd", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "Inkoop van ${totalPurchasePrice.toCurrencyString()} opgeslagen", Toast.LENGTH_SHORT).show()
            }
            reset()
            db.mergeTransactionsIfPossible(System.currentTimeMillis())
            val changes = AchievementManager.updateAchievementsAfterBuy(person)
            (this.activity as CelebratingHuisEtActivity).showAchievements(changes)

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
            val person = db.getPersonWithId(newPickedId)
            val boughtWhatTv = view.findViewById<TextView>(R.id.whatHaveYouBoughtText)
            boughtWhatTv.text = "${person!!.name}, Wat heb je gekocht?"
            userLayout.visibility = View.GONE
            productLayout.visibility = View.VISIBLE
        }
    }

    fun increaseCounter(inc: Int) {
        totalPurchasePrice += inc
    }

    fun onCreateNewProductClicked() {
        val intent = Intent(this.context, EditProductActivity::class.java)
        startActivityForResult(intent,2)
    }
    
    private fun reset(){
        // this resets the chosen person to none, putting you back to the pick person screen
        setPersonAndUpdate(null)
        // clear amounts in recyclerview
        prodRecAdapter.resetMapValues()
        totalPurchasePrice = 0
        depositPrice = 0
    }

    fun increaseCounter(productPriceInc: Int, depositInc: Int) {
        totalPurchasePrice += productPriceInc
        if (depositEnabled) depositPrice += depositInc
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("pickedPersonId", pickedPersonId)
        outState.putInt("totalPurchasePrice",totalPurchasePrice)
        prodRecAdapter.saveOutState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState!= null){
            totalPurchasePrice = savedInstanceState.getInt("totalPurchasePrice")
            pickedPersonId = savedInstanceState.getString("pickedPersonId")
            setPersonAndUpdate(pickedPersonId)
            prodRecAdapter.restoreInstanceState(savedInstanceState)
        }
    }



}