import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.adapters.AmountMainRecAdapter
import com.tobo.huiset.gui.adapters.ProductMainRecAdapter
import com.tobo.huiset.gui.adapters.TransactionRecAdapter
import com.tobo.huiset.gui.adapters.TurfRecAdapter
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.utils.extensions.toPixel
import f.tom.consistentspacingdecoration.ConsistentSpacingDecoration
import io.realm.Sort


class FragmentMain : HuisEtFragment() {

    private lateinit var spacer: ConsistentSpacingDecoration

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

        setupAmountRec(view)
        setupProductRec(view)

        val transActionRec = setupTransactionsRec(view)
        setupTurfRec(view, transActionRec)

        if (savedInstanceState != null) {
            val amountAdapter = view.findViewById<RecyclerView>(R.id.mainAmountRec)?.adapter as AmountMainRecAdapter
            amountAdapter.selectedPos = savedInstanceState.getInt("selectedPos")
            amountAdapter.notifyDataSetChanged()
        }
    }

    override fun onTabReactivated() {
        db.selectFirstTurfProduct()
        val turfRec = view?.findViewById<RecyclerView>(R.id.mainPersonRec)
        val adapter = turfRec!!.adapter as TurfRecAdapter

        val columns = getNumOfColumns(adapter.itemCount)
        turfRec.layoutManager = GridLayoutManager(this.context,columns)
        setupSpacingForTurfRec(columns)
    }

    private fun setupAmountRec(view: View): RecyclerView {
        // this sets up the amount recyclerview
        val amountRec = view.findViewById<RecyclerView>(R.id.mainAmountRec)
        val amountList = (1..20).toList()
        amountRec.adapter = AmountMainRecAdapter(amountList, this.context!!)
        amountRec.layoutManager = GridLayoutManager(this.context, 1, GridLayoutManager.HORIZONTAL, false)
        amountRec.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))


        return amountRec
    }

    private fun setupProductRec(view: View): RecyclerView {
        val products = db.findAllCurrentProducts(Product.ONLY_TURFABLE)

        // this sets up the product recyclerview
        val prodRec = view.findViewById<RecyclerView>(R.id.mainProductRec)
        prodRec.adapter = ProductMainRecAdapter(this.context!!, products, true)
        prodRec.layoutManager = GridLayoutManager(this.context, 1, GridLayoutManager.HORIZONTAL, false)
        prodRec.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        ItemClickSupport.addTo(prodRec).setOnItemClickListener { _, position, _ ->
            db.selectProduct(productToSelect = products[position])
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
        val amountRec = view.findViewById<RecyclerView>(R.id.mainAmountRec)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions, amountRec, realm, true)
        transActionRec.layoutManager = LinearLayoutManager(this.context)
        transActionRec.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        //init the periodic refresh
        transactionTimeRefreshHandler = Handler()
        transactionTimeRefreshHandler!!.postDelayed(updateTransactionRecRunnable, 0)

        return transActionRec
    }

    /**
     * This sets up the right recyclerview containing the persons that can be tapped to add a beer.
     */
    private fun setupTurfRec(view: View, transitionRec: RecyclerView) {

        val profiles = db.findAllCurrentPersons()
        val columns = this.getNumOfColumns(profiles.count())

        val turfRec = view.findViewById<RecyclerView>(R.id.mainPersonRec)
        turfRec.adapter = TurfRecAdapter(this.context!!, profiles, true)
        turfRec.layoutManager = GridLayoutManager(this.context, columns)

        setupSpacingForTurfRec(columns)

        val amountAdapter = view.findViewById<RecyclerView>(R.id.mainAmountRec).adapter as AmountMainRecAdapter

        ItemClickSupport.addTo(turfRec).setOnItemClickListener { _, position, _ ->
            val person = profiles[position]
            if (person != null) {

                db.doTransactionWithSelectedProduct(person, amountAdapter.getSelectedAmount())
                db.selectFirstTurfProduct()

                amountAdapter.resetAmountToFirst()

                //scroll to the top, because the item is added at the top
                transitionRec.scrollToPosition(0)
            }
        }
    }

    private fun getNumOfColumns(amountOfProfilesToShow: Int):Int{
        val displayMetrics = context!!.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        return when{
            amountOfProfilesToShow >= 8 && dpWidth > 1200 -> 4 // large 10 inch tablets in landscape
            amountOfProfilesToShow >= 7 && dpWidth > 900 -> 3
            amountOfProfilesToShow >= 4 && dpWidth > 600 -> 2 // 7 inch tablet in portrait
            else -> 1
        }
    }

    private fun setupSpacingForTurfRec(columns: Int) {
        val turfRec = view!!.findViewById<RecyclerView>(R.id.mainPersonRec)
        if(::spacer.isInitialized){
            turfRec.removeItemDecoration(spacer)
        }
        spacer =  ConsistentSpacingDecoration(16.toPixel(this.context!!), 16.toPixel(this.context!!), columns)
        turfRec.addItemDecoration(spacer)
    }

    // it saves state when tablet is flipped
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val amountAdapter = view?.findViewById<RecyclerView>(R.id.mainAmountRec)?.adapter as AmountMainRecAdapter
        outState.putInt("selectedPos", amountAdapter.selectedPos)
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


