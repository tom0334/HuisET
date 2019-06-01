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
import com.tobo.huiset.gui.adapters.TransactionRecAdapter
import com.tobo.huiset.gui.adapters.TurfRecAdapter
import com.tobo.huiset.utils.extensions.getBeerProduct
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.toPixel
import f.tom.consistentspacingdecoration.ConsistentSpacingDecoration
import io.realm.Sort


class FragmentMain : HuisEtFragment() {

    private val TRANSACTION_VIEW_REFRESH_TIME = 5000L

    private var transactionTimeRefreshHandler: Handler?  = null


    val updateTransactionRecRunnable = object : Runnable {
        override fun run() {
            updateTransactionRec(this)
        }
    }
    /**
     * This function is run every TRANSACTION_VIEW_REFRESH_TIME milliseconds. It updates the time ago texts in the
     * transactionview.
     */
    fun updateTransactionRec(parentRunnable: Runnable){
        val rec = this.view?.findViewById<RecyclerView>(R.id.recentRecyclerView) ?: return
        rec.adapter?.notifyDataSetChanged()
        transactionTimeRefreshHandler!!.postDelayed(parentRunnable, TRANSACTION_VIEW_REFRESH_TIME)
    }

    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transActionRec = setupTransactionsRec(view)
        setupTurfRec(view, transActionRec)
    }

    /**
     * This sets up the transactionsrec on the left side that shows the previous beers.
     *
     * It is updated automatically
     */
    private fun setupTransactionsRec(view: View) : RecyclerView{
        val transactions = realm.where(Transaction::class.java)
            .sort("time", Sort.DESCENDING)
            .findAll()


        val transActionRec = view.findViewById<RecyclerView>(R.id.recentRecyclerView)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions,realm, true)
        transActionRec.layoutManager = LinearLayoutManager(this.context)

        //init the periodic refresh
        transactionTimeRefreshHandler = Handler()
        transactionTimeRefreshHandler!!.postDelayed(updateTransactionRecRunnable, 0)

        return transActionRec
    }


    /**
     * This sets up the right recyclerview containing the persons that can be tapped to add a beer.
     */
    private fun setupTurfRec(view: View, transitionRec:RecyclerView) {
        val columns = 2

        val profiles = realm.where(Person::class.java).findAll()
        val turfRec = view.findViewById<RecyclerView>(com.tobo.huiset.R.id.mainPersonRec)
        val adapter = TurfRecAdapter(this.context!!, profiles, realm, true)

        turfRec.adapter = adapter
        turfRec.layoutManager = GridLayoutManager(this.context, columns)

        val spacer = ConsistentSpacingDecoration(16.toPixel(this.context!!),16.toPixel(this.context!!),columns)
        turfRec.addItemDecoration(spacer)


        ItemClickSupport.addTo(turfRec).setOnItemClickListener { recyclerView, position, v ->
            val person = profiles.get(position)
            if(person != null){
                realm.executeTransaction {
                    val t = Transaction.create(person, realm.getBeerProduct())
                    person.addTransaction(t)
                    //scroll to the top, because the item is added at the top
                    transitionRec.scrollToPosition(0)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val rec: RecyclerView? = view?.findViewById(R.id.mainPersonRec)
        if(rec != null){
            ItemClickSupport.removeFrom(rec)
        }
        if(transactionTimeRefreshHandler != null){
            transactionTimeRefreshHandler!!.removeCallbacks(updateTransactionRecRunnable)

        }
    }


}


