import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tobo.huiset.R
import com.tobo.huiset.achievements.AchievementManager
import com.tobo.huiset.extendables.CelebratingHuisEtActivity
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.activities.MainActivity
import com.tobo.huiset.gui.activities.PREFS_TURF_CONFETTI_ID
import com.tobo.huiset.gui.adapters.AmountMainRecAdapter
import com.tobo.huiset.gui.adapters.ProductMainRecAdapter
import com.tobo.huiset.gui.adapters.TransactionRecAdapter
import com.tobo.huiset.gui.adapters.TurfRecAdapter
import com.tobo.huiset.realmModels.AchievementCompletion
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.utils.extensions.toPixel
import f.tom.consistentspacingdecoration.ConsistentSpacingDecoration
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*


class FragmentMain : HuisEtFragment() {

    private lateinit var spacer: ConsistentSpacingDecoration

    private val TRANSACTION_VIEW_REFRESH_TIME = 5000L

    private var transactionTimeRefreshHandler: Handler? = null

    private var mergeTransactionsHandler:Handler = Handler()

    private val showConfettiOnTurf by lazy{ PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean(PREFS_TURF_CONFETTI_ID,false)}

    private val updateTransactionRecRunnable = object : Runnable {
        override fun run() {
            updateTransactionRec(this)
        }
    }

    private val mergeTransactionsRunnable = Runnable {
        db.mergeTransactionsIfPossible( System.currentTimeMillis()- 30 * 1000)
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

        confirmationChecks()

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

        confirmationChecks()

        db.selectFirstTurfProduct()

        val turfRec = view?.findViewById<RecyclerView>(R.id.mainPersonRec)
        val adapter = turfRec!!.adapter as TurfRecAdapter

        val columns = getNumOfColumns(adapter.itemCount)
        turfRec.layoutManager = GridLayoutManager(this.context,columns)
        setupSpacingForTurfRec(columns)
        db.mergeTransactionsIfPossible(System.currentTimeMillis())
    }

    private fun confirmationChecks() {
        val needToCheckPerson = checkIfAnyTurfableProductExists()
        if (needToCheckPerson) checkIfAnyShownPersonExists()
    }

    private fun checkIfAnyTurfableProductExists(): Boolean {
        if (db.findAllCurrentProducts(Product.ONLY_TURFABLE).size <= 0) {
            val context = this.context as MainActivity

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Er zijn geen turfbare producten gekozen.")
                .setPositiveButton("Naar \"Producten\"") { _, _ ->
                    context.showFragment(context.PRODUCTS_TAB)
                    val bottomNavigation =
                        context.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                    bottomNavigation.selectedItemId =
                        bottomNavigation.menu.getItem(context.PRODUCTS_TAB).itemId
                }

            // Create the AlertDialog object and return it
            builder.create().show()

            return false
        }
        return true
    }

    private fun checkIfAnyShownPersonExists() {
        if (db.findAllCurrentPersons(false).size <= 0) {
            val context = this.context as MainActivity

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Er worden momenteel geen gebruikers getoond.")
                .setPositiveButton("Naar \"Gebruikers\"") { _, _ ->
                    context.showFragment(context.PROFILES_TAB)
                    val bottomNavigation = context.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                    bottomNavigation.selectedItemId = bottomNavigation.menu.getItem(context.PROFILES_TAB).itemId
                }

            // Create the AlertDialog object and return it
            builder.create().show()
        }
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

        val onDeleteClicked = fun (trans: Transaction, person: Person){
            if (trans.otherPerson == null) {
                db.deleteTransaction(trans)
            } else {
                db.deleteTransaction(trans)
            }

            //When removing transactions, it can happen that some achievements should not have been completed.
            //It can also happen that removing a transaction has the result of unlocking an achivement for someone else or himself
            //Keep track of what achievements were added, and show that in the activity
            val added = mutableListOf<AchievementCompletion>()
            db.findAllCurrentPersons(true).forEach { _ ->
                added.addAll(AchievementManager.checkAgainForPerson(person))
            }
            (this.activity as CelebratingHuisEtActivity).showAchievements(added)

        }


        val transActionRec = view.findViewById<RecyclerView>(R.id.recentRecyclerView)
        val amountRec = view.findViewById<RecyclerView>(R.id.mainAmountRec)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions, amountRec, realm, true, onDeleteClicked)
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

        val profiles = db.findAllCurrentPersons(false)
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
                val changed = AchievementManager.updateAchievementsAfterTurf(person)
                (activity as MainActivity).showAchievements(changed)

                if(changed.isEmpty() && showConfettiOnTurf){
                    (activity as MainActivity).showTurfConfetti(person)
                }

                db.selectFirstTurfProduct()

                amountAdapter.resetAmountToFirst()

                //scroll to the top, because the item is added at the top
                transitionRec.scrollToPosition(0)
                mergeTransactionsHandler.postDelayed(mergeTransactionsRunnable,30 * 1000)

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
        db.mergeTransactionsIfPossible(System.currentTimeMillis())
        mergeTransactionsHandler!!.removeCallbacks(mergeTransactionsRunnable)

    }


}


