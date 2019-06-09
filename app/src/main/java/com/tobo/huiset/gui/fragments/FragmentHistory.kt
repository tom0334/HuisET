import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.HistoryAdapter
import com.tobo.huiset.gui.adapters.HistoryItem
import com.tobo.huiset.gui.adapters.HistoryPersonRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.utils.extensions.getProductWithId
import java.util.*
import java.text.SimpleDateFormat


public class FragmentHistory : HuisEtFragment() {

    lateinit var historyAdapter:HistoryAdapter
    var lateTimePoint:Long = 0
    var earlyTimePoint:Long = 0


    //24h in millis
    var timeDiff = 86400000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTimePoints(view)
        setupPersonRec(view)
        setupHistoryRec(view)


        view.findViewById<Button>(R.id.historyGoBackwardsButton).setOnClickListener {
            this.earlyTimePoint -= timeDiff
            this.lateTimePoint-= timeDiff
            updateHistory()
            updateTimePointsText(view)

        }

        view.findViewById<Button>(R.id.historyGoFowardsButton).setOnClickListener {
            this.earlyTimePoint += timeDiff
            this.lateTimePoint+= timeDiff
            updateHistory()
            updateTimePointsText(view)
        }

    }

    private fun setupPersonRec(view:View){
        val historyPersonRec = view.findViewById<RecyclerView>(R.id.historyPersonRec)

        val persons = mutableListOf<Person?>(null)
        persons.addAll(realm.where(Person::class.java).findAll())

        val adapter = HistoryPersonRecAdapter(persons,this.context!!, realm)
        historyPersonRec.adapter  = adapter
        historyPersonRec.layoutManager = LinearLayoutManager(this.context!!)

        ItemClickSupport.addTo(historyPersonRec).setOnItemClickListener { recyclerView, position, v ->
            if(position == -1) return@setOnItemClickListener // this happens when clicking 2 at the same time
            val p = persons[position]
            realm.executeTransaction {
                realm.where(Person::class.java).findAll().forEach { it.isSelectedInHistoryView = false }
                if(p != null) p.isSelectedInHistoryView = true
            }
            adapter.notifyDataSetChanged()
            updateHistory()
        }
    }

    private fun updateHistory() {
        this.historyAdapter.items.clear()
        this.historyAdapter.items.addAll(findHistoryItems())
        this.historyAdapter.notifyDataSetChanged()
    }

    private fun setupHistoryRec(view:View){
        val historyRec= view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        val data =mutableListOf<HistoryItem>()
        data.addAll(findHistoryItems())
        this.historyAdapter = HistoryAdapter(data, this.context!!)
        historyRec.adapter = historyAdapter
        historyRec.layoutManager = LinearLayoutManager(this.context!!)
    }



    private fun findHistoryItems(): List<HistoryItem>{
        val selectedPerson = realm.where(Person::class.java).equalTo("selectedInHistoryView",true).findFirst()

        val transactions = when (selectedPerson) {
            null -> realm.where(Transaction::class.java).findAll()
            else -> realm.where(Transaction::class.java).equalTo("personId", selectedPerson.id).findAll()
        }


        val inTimeSpan = transactions.where().between("time", earlyTimePoint!!, lateTimePoint!!).findAll()

        return  inTimeSpan
            .groupBy { it.productId}
            .map { (id, values) -> HistoryItem(realm.getProductWithId(id)!!, values.size) }
            .sortedByDescending { it.amount }

    }

    private fun initTimePoints(view: View){
        lateTimePoint = System.currentTimeMillis()
        earlyTimePoint = lateTimePoint!! - timeDiff
        updateTimePointsText(view)
    }

    private fun updateTimePointsText(view: View) {
        val timeFormat =  SimpleDateFormat("HH:mm")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")


        val lateTimepointDay = view.findViewById<TextView>(R.id.lateTimePointDate)
        val lateTimePointtime = view.findViewById<TextView>(R.id.lateTimePointTime)
        val earlyTimePointDay = view.findViewById<TextView>(R.id.earlyTimePointDate)
        val earlyTimePointTime = view.findViewById<TextView>(R.id.earlyTimePointTime)

        earlyTimePointDay.text = dateFormat.format(Date(earlyTimePoint!!))
        lateTimepointDay.text = dateFormat.format(Date(lateTimePoint!!))

        earlyTimePointTime.text = timeFormat.format(Date(earlyTimePoint!!))
        lateTimePointtime.text =timeFormat.format(Date(lateTimePoint!!))

    }

    override fun onDestroy() {
        super.onDestroy()
        val rec = view?.findViewById<RecyclerView>(R.id.historyPersonRec)
        if(rec!= null)ItemClickSupport.removeFrom(rec)

    }


}