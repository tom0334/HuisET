import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.adapters.HistoryAdapter
import com.tobo.huiset.gui.adapters.HistoryItem
import com.tobo.huiset.gui.adapters.HistoryPersonRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.ItemClickSupport
import java.text.SimpleDateFormat
import java.util.*

class FragmentHistory : HuisEtFragment() {

    private lateinit var personAdap: HistoryPersonRecAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private var lateTimePoint: Long = 0
    private var earlyTimePoint: Long = 0

    private var showBuy = false


    private val timeNames =
        arrayOf<CharSequence>("1 uur", "8 uur", "1 dag", "1 week", "1 maand", "3 maanden", "6 maanden", "1 jaar")

    private val TIMEDIFF_ONE_HOUR = 0
    private val TIMEDIFF_EIGHT_HOURS = 1
    private val TIMEDIFF_ONE_DAY = 2
    private val TIMEDIFF_ONE_WEEK = 3
    private val TIMEDIFF_ONE_MONTH = 4
    private val TIMEDIFF_THREE_MONTHS = 5
    private val TIMEDIFF_HALF_YEAR = 6
    private val TIMEDIFF_YEAR = 7

    private var timeDiffSelected: Int = TIMEDIFF_ONE_DAY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onTabReactivated(){
        initTimePoints(view!!)
        updatePersons()
        updateHistory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTimePoints(view)
        setupPersonRec(view)
        setupHistoryRec(view)


        view.findViewById<Button>(R.id.historyGoBackwardsButton).setOnClickListener {
            this.lateTimePoint = getAdvancedTime(lateTimePoint, backwards = true)
            this.earlyTimePoint = getAdvancedTime(earlyTimePoint, backwards = true)
            updateHistory()
            updateTimePointsText()
        }

        view.findViewById<Button>(R.id.historyGoFowardsButton).setOnClickListener {
            val newLate = getAdvancedTime(lateTimePoint, backwards = false)

            if (newLate > System.currentTimeMillis()) {
                Toast.makeText(
                    this.context!!,
                    "Je probeert de toekomst te bekijken. Daar is nog niks geturft!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            this.lateTimePoint = getAdvancedTime(lateTimePoint, backwards = false)
            this.earlyTimePoint = getAdvancedTime(earlyTimePoint, backwards = false)
            updateHistory()
            updateTimePointsText()
        }
        view.findViewById<Button>(R.id.pickPeriodButton).setOnClickListener {
            showPickPeriodDialog()
        }

        val radioGroup = view.findViewById<RadioGroup>(R.id.radiogroup_history_bought)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            showBuy = checkedId == R.id.radioHistoryBought
            updateHistory()
            updateTimePointsText()
        }


    }

    private fun showPickPeriodDialog() {

        // Creating and Building the Dialog
        val builder = AlertDialog.Builder(this.context!!)
        builder.setTitle("Kies tijdperiode")


        builder.setSingleChoiceItems(timeNames, timeDiffSelected
        ) { dialog, item: Int ->
            timeDiffSelected = item

            lateTimePoint = System.currentTimeMillis()
            earlyTimePoint = getAdvancedTime(lateTimePoint, true)

            updateTimePointsText()
            updateHistory()
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setupPersonRec(view: View) {
        val historyPersonRec = view.findViewById<RecyclerView>(R.id.historyPersonRec)
        personAdap = HistoryPersonRecAdapter(mutableListOf(), this.context!!, realm)
        historyPersonRec.adapter = personAdap
        historyPersonRec.layoutManager = LinearLayoutManager(this.context!!)

        ItemClickSupport.addTo(historyPersonRec).setOnItemClickListener { _, position, _ ->
            if (position == -1) return@setOnItemClickListener // this happens when clicking 2 at the same time
            val p = personAdap.items[position]
            db.selectPersonInHistory(p)
            personAdap.notifyDataSetChanged()
            updateHistory()
        }
        updatePersons()
    }

    private fun updateHistory() {
        val newData = findHistoryItems()
        val historyRec = view!!.findViewById<RecyclerView>(R.id.historyRecyclerView)

        //the views to show if there is no data
        val noDataView = view!!.findViewById<View>(R.id.history_nodata_view)
        val noDataTextView = view!!.findViewById<TextView>(R.id.text_nothing_turfed)

        this.historyAdapter.items.clear()
        if (newData.isEmpty()) {
            historyRec.visibility = View.GONE
            noDataView.visibility = View.VISIBLE

            val selectedPerson = db.getSelectedPersonInHistory()

            noDataTextView.text = when{
                selectedPerson != null && showBuy -> "${selectedPerson.name} heeft niets gekocht deze periode!"
                selectedPerson != null && !showBuy -> "${selectedPerson.name} heeft niets geturft deze periode!"
                selectedPerson == null && !showBuy -> "Niemand heeft iets geturft deze periode!"
                else-> "Niemand heeft iets gekocht deze periode!"

            }
        } else {
            this.historyAdapter.items.addAll(newData)
            historyRec.visibility = View.VISIBLE
            noDataView.visibility = View.GONE
        }

        this.historyAdapter.notifyDataSetChanged()
    }

    private fun updatePersons(){
        val persons = mutableListOf<Person?>(null)
        persons.addAll(db.findPersonsIncludingDeleted())
        personAdap.items.clear()
        personAdap.items.addAll(persons)
        personAdap.notifyDataSetChanged()
    }

    private fun setupHistoryRec(view: View) {
        val historyRec = view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        val data = mutableListOf<HistoryItem>()
        this.historyAdapter = HistoryAdapter(data, this.context!!)
        historyRec.adapter = historyAdapter
        historyRec.layoutManager = LinearLayoutManager(this.context!!)
        updateHistory()
    }


    private fun findHistoryItems(): List<HistoryItem> {

        val transactions = when (val selectedPerson = db.getSelectedPersonInHistory()) {
            null -> realm.where(Transaction::class.java).findAll()
            else -> realm.where(Transaction::class.java).equalTo("personId", selectedPerson.id).findAll()
        }

        val inTimeSpan = transactions.where().between("time", earlyTimePoint, lateTimePoint).findAll()

        /**
         * class key starts with a lowercase letter, because it can't be found otherwise
         */
        data class key(val productId: String, val isBuy: Boolean)
        fun Transaction.tokey(): key {
            return key(this.productId, this.isBuy)
        }
        val res = inTimeSpan
            .asSequence()
            .filter { it.isBuy == showBuy}
            .groupBy { it.tokey()}
            .map { (key, values) -> HistoryItem(db.getProductWithId(key.productId)!!.name, values.sumBy { it.amount }, values.sumBy { it.saldoImpact }, false) }
            .sortedByDescending { it.amount }.toMutableList()


        if(res.isEmpty()) return res.toList()


        val totalAmount = res.sumBy { it.amount }
        val totalPrice = res.sumBy { it.price }
        res.add(HistoryItem("Totaal", totalAmount, totalPrice, true))
        return res.toList()

    }

    /**
     * param view is mandatory
     */
    private fun initTimePoints(view: View) {
        lateTimePoint = System.currentTimeMillis()
        earlyTimePoint = getAdvancedTime(lateTimePoint, backwards = true)
        updateTimePointsText()
    }


    private fun getAdvancedTime(from: Long, backwards: Boolean): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = from

        val multiplier = if (backwards) -1 else 1

        when (timeDiffSelected) {
            TIMEDIFF_ONE_HOUR -> cal.add(Calendar.HOUR, multiplier * 1)
            TIMEDIFF_EIGHT_HOURS -> cal.add(Calendar.HOUR, multiplier * 8)
            TIMEDIFF_ONE_DAY -> cal.add(Calendar.DAY_OF_YEAR, multiplier * 1)
            TIMEDIFF_ONE_WEEK -> cal.add(Calendar.WEEK_OF_YEAR, multiplier * 1)
            TIMEDIFF_ONE_MONTH -> cal.add(Calendar.MONTH, multiplier * 1)
            TIMEDIFF_THREE_MONTHS -> cal.add(Calendar.MONTH, multiplier * 3)
            TIMEDIFF_HALF_YEAR -> cal.add(Calendar.MONTH, multiplier * 6)
            TIMEDIFF_YEAR -> cal.add(Calendar.YEAR, multiplier * 1)
        }
        return cal.time.time
    }


    private fun updateTimePointsText() {
        val view = this.view!!
        val timeFormat = SimpleDateFormat("HH:mm")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")


        val lateTimepointDay = view.findViewById<TextView>(R.id.lateTimePointDate)
        val lateTimePointtime = view.findViewById<TextView>(R.id.lateTimePointTime)
        val earlyTimePointDay = view.findViewById<TextView>(R.id.earlyTimePointDate)
        val earlyTimePointTime = view.findViewById<TextView>(R.id.earlyTimePointTime)

        val timeDifTv = view.findViewById<TextView>(R.id.timeDiffText)
        timeDifTv.text = timeNames[timeDiffSelected]

        earlyTimePointDay.text = dateFormat.format(Date(earlyTimePoint))
        lateTimepointDay.text = dateFormat.format(Date(lateTimePoint))

        earlyTimePointTime.text = timeFormat.format(Date(earlyTimePoint))
        lateTimePointtime.text = timeFormat.format(Date(lateTimePoint))

    }

    override fun onDestroy() {
        super.onDestroy()
        val rec = view?.findViewById<RecyclerView>(R.id.historyPersonRec)
        if (rec != null) ItemClickSupport.removeFrom(rec)

    }


}