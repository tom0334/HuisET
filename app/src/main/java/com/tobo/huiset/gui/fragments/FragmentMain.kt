import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.ProductMainRecAdapter
import com.tobo.huiset.gui.adapters.TransactionRecAdapter
import com.tobo.huiset.gui.adapters.TurfRecAdapter
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.executeSafe
import com.tobo.huiset.utils.extensions.getFirstProduct
import com.tobo.huiset.utils.extensions.toPixel
import f.tom.consistentspacingdecoration.ConsistentSpacingDecoration
import io.realm.Sort


class FragmentMain : HuisEtFragment() {

    private val TRANSACTION_VIEW_REFRESH_TIME = 5000L

    private var transactionTimeRefreshHandler: Handler? = null

    private val updateTransactionRecRunnable = object : Runnable {
        override fun run() {
            updateTransactionRec(this)
        }
    }

    /**
     * This function is run every TRANSACTION_VIEW_REFRESH_TIME milliseconds. It updates the time ago texts in the
     * transactionview.
     */
    fun updateTransactionRec(parentRunnable: Runnable) {
        val rec = this.view?.findViewById<RecyclerView>(R.id.recentRecyclerView) ?: return
        rec.adapter?.notifyDataSetChanged()
        transactionTimeRefreshHandler!!.postDelayed(parentRunnable, TRANSACTION_VIEW_REFRESH_TIME)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transActionRec = setupTransactionsRec(view)
        setupTurfRec(view, transActionRec)

        setupProductRec(view)
    }

    private fun setupProductRec(view: View): RecyclerView {
        val products = realm.where(Product::class.java)
            .equalTo("deleted", false)
            .equalTo("show", true)
            .findAll()

        // this sets up the recyclerview to show the products
        val prodRec = view.findViewById<RecyclerView>(R.id.mainProductRec)
        prodRec.adapter = ProductMainRecAdapter(this.context!!, products, true)
        prodRec.layoutManager = GridLayoutManager(this.context, 1, GridLayoutManager.HORIZONTAL, false)

        ItemClickSupport.addTo(prodRec).setOnItemClickListener { _, position, _ ->
            realm.executeTransaction {
                realm.where(Product::class.java)
                    .equalTo("deleted", false)
                    .equalTo("selected", true)
                    .findAll()
                    .forEach {
                        it.isSelected = false
                    }
                products[position]?.isSelected = true
            }
        }
        return prodRec
    }

    /**
     * This sets up the transactionsrec on the left side that shows the previous beers.
     *
     * It is updated automatically
     */
    private fun setupTransactionsRec(view: View): RecyclerView {
        val transactions = realm.where(Transaction::class.java)
            .sort("time", Sort.DESCENDING)
            .findAll()


        val transActionRec = view.findViewById<RecyclerView>(R.id.recentRecyclerView)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions, realm, true)
        transActionRec.layoutManager = LinearLayoutManager(this.context)

        //init the periodic refresh
        transactionTimeRefreshHandler = Handler()
        transactionTimeRefreshHandler!!.postDelayed(updateTransactionRecRunnable, 0)

        return transActionRec
    }

    /**
     * This sets up the right recyclerview containing the persons that can be tapped to add a beer.
     */
    private fun setupTurfRec(view: View, transitionRec: RecyclerView) {
        val columns = 2

        val profiles = realm.where(Person::class.java)
            .equalTo("deleted", false)
            .equalTo("show", true)
            .findAll()

        val turfRec = view.findViewById<RecyclerView>(R.id.mainPersonRec)
        turfRec.adapter = TurfRecAdapter(this.context!!, profiles, true)
        turfRec.layoutManager = GridLayoutManager(this.context, columns)

        val spacer = ConsistentSpacingDecoration(16.toPixel(this.context!!), 16.toPixel(this.context!!), columns)
        turfRec.addItemDecoration(spacer)

        ItemClickSupport.addTo(turfRec).setOnItemClickListener { _, position, _ ->
            val person = profiles[position]
            if (person != null) {
                realm.executeSafe {
                    val selectedProduct = realm.where(Product::class.java)
                        .equalTo("deleted", false)
                        .equalTo("selected", true)
                        .findFirst()
                    val t = Transaction.create(person, selectedProduct, false)
                    selectedProduct?.isSelected = false

                    realm.copyToRealmOrUpdate(t)
                    person.addTransaction(t)
                }

                realm.executeTransaction {
                    val firstProd = realm.getFirstProduct()
                    // select 1st product
                    if (firstProd != null) {
                        firstProd.isSelected = true
                    }
                }

                //scroll to the top, because the item is added at the top
                transitionRec.scrollToPosition(0)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val rec: RecyclerView? = view?.findViewById(R.id.mainPersonRec)
        if (rec != null) {
            ItemClickSupport.removeFrom(rec)
        }
        if (transactionTimeRefreshHandler != null) {
            transactionTimeRefreshHandler!!.removeCallbacks(updateTransactionRecRunnable)

        }
    }


}


