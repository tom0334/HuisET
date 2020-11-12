import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
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
import com.tobo.huiset.utils.extensions.sumByFloat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class FragmentHistory : HuisEtFragment() {

    val SHOW_BOUGHT = 0
    val SHOW_TURFED = 1
    val SHOW_NETTO = 2

    private lateinit var personAdap: HistoryPersonRecAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private var lateTimePoint: Long = 0
    private var earlyTimePoint: Long = 0

    private var showBuy = SHOW_TURFED

    private val timeNames =
        arrayOf<CharSequence>("1 uur", "8 uur", "1 dag", "1 week", "1 maand", "3 maanden", "6 maanden", "1 jaar", "Altijd")

    private val TIMEDIFF_ONE_HOUR = 0
    private val TIMEDIFF_EIGHT_HOURS = 1
    private val TIMEDIFF_ONE_DAY = 2
    private val TIMEDIFF_ONE_WEEK = 3
    private val TIMEDIFF_ONE_MONTH = 4
    private val TIMEDIFF_THREE_MONTHS = 5
    private val TIMEDIFF_HALF_YEAR = 6
    private val TIMEDIFF_YEAR = 7
    private val TIMEDIFF_ALWAYS = 8

    private var timeDiffSelected: Int = TIMEDIFF_ONE_DAY

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onTabReactivated(userTapped: Boolean){
        initTimePoints(view!!)
        updatePersons()
        updateHistory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTimePoints(view)
        setupPersonRec(view)
        setupHistoryRec(view)


        view.findViewById<ImageButton>(R.id.historyGoBackwardsButton).setOnClickListener {
            val newEarly = getAdvancedTime(earlyTimePoint, backwards = true)
            if (newEarly < 0) {
                Toast.makeText(
                    this.context!!,
                    "Oeps, onze tijdmachine gaat niet verder terug dan dit.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            this.earlyTimePoint = newEarly
            this.lateTimePoint = getAdvancedTime(lateTimePoint, backwards = true)
            updateHistory()
            updateTimePointsText()
        }

        view.findViewById<ImageButton>(R.id.historyGoFowardsButton).setOnClickListener {
            val newLate = getAdvancedTime(lateTimePoint, backwards = false)
            if (newLate > System.currentTimeMillis()) {
                Toast.makeText(
                    this.context!!,
                    "Je probeert de toekomst te bekijken. Daar is nog niks geturft!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            this.lateTimePoint = newLate
            this.earlyTimePoint = getAdvancedTime(earlyTimePoint, backwards = false)
            updateHistory()
            updateTimePointsText()
        }
        view.findViewById<Button>(R.id.pickPeriodButton).setOnClickListener {
            showPickPeriodDialog()
        }

        val radioGroup = view.findViewById<RadioGroup>(R.id.radiogroup_history_bought)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            showBuy = when (checkedId) {
                R.id.radioHistoryBought -> SHOW_BOUGHT
                R.id.radioHistoryGeturft -> SHOW_TURFED
                R.id.radioHistoryNetto -> SHOW_NETTO
                else -> SHOW_TURFED
            }
            updateHistory()
            updateTimePointsText()
        }


    }

    private fun showPickPeriodDialog() {

        // Creating and Building the Dialog
        val builder = AlertDialog.Builder(this.context!!)
        builder.setTitle("Kies tijdperiode")


        builder.setSingleChoiceItems(
            timeNames, timeDiffSelected
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
        historyPersonRec.addItemDecoration(DividerItemDecoration(historyPersonRec.context, DividerItemDecoration.VERTICAL))
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

        //the views to show if there is no dateMap
        val noDataView = view!!.findViewById<View>(R.id.history_nodata_view)
        val noDataTextView = view!!.findViewById<TextView>(R.id.text_nothing_turfed)

        this.historyAdapter.items.clear()
        if (newData.isEmpty()) {
            historyRec.visibility = View.GONE
            noDataView.visibility = View.VISIBLE

            val selectedPerson = db.getSelectedPersonInHistory()

            noDataTextView.text = when{
                selectedPerson != null && showBuy == SHOW_BOUGHT -> "${selectedPerson.name} heeft niets gekocht deze periode!"
                selectedPerson == null && showBuy == SHOW_BOUGHT -> "Niemand heeft iets gekocht deze periode!"
                selectedPerson != null && showBuy == SHOW_TURFED -> "${selectedPerson.name} heeft niets geturft deze periode!"
                selectedPerson == null && showBuy == SHOW_TURFED -> "Niemand heeft iets geturft deze periode!"
                selectedPerson != null && showBuy == SHOW_NETTO -> "De balans van ${selectedPerson.name} is niet veranderd deze periode!"
                selectedPerson == null && showBuy == SHOW_NETTO -> "De totale balans is niet veranderd deze periode!"
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

        val selectedPerson = db.getSelectedPersonInHistory()
        val transactions = when (selectedPerson) {
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
        var res = inTimeSpan
            .asSequence()
            .filter { it.isBuy == (showBuy == SHOW_BOUGHT) }
            .filter { it.product != null }
            .groupBy { it.tokey() }
            .map { (key, values) -> HistoryItem(
                    db.getProductWithId(key.productId)!!.name,
                    values.sumByFloat { it.amount },
                    values.sumByFloat { it.saldoImpact }.roundToInt(),
                    false
                )
            }
            .sortedByDescending { it.amount }
            .toMutableList()

        if (showBuy == SHOW_NETTO) {
            // calculate bought items in a similar fashion
            var buyRes = inTimeSpan
                .asSequence()
                .filter { it.isBuy }
                .filter { it.product != null }
                .groupBy { it.tokey() }
                .map { (key, values) ->
                    HistoryItem(
                        db.getProductWithId(key.productId)!!.name,
                        values.sumByFloat { it.amount },
                        values.sumByFloat { it.saldoImpact }.roundToInt(),
                        false
                    )
                }
                .sortedBy { it.productName }

            // if res item and bought item are the same, subtract amount and add priceImpacts.
            res = res.map { t ->
                if (buyRes.any { b -> b.productName == t.productName }) {
                    val b = buyRes.first { b -> b.productName == t.productName }
                    buyRes = buyRes.minus(b)
                    HistoryItem(
                        t.productName,
                        b.amount - t.amount,
                        b.price + t.price,
                        false
                    )
                } else {
                    t
                }
            }.toMutableList()
            res = res.union(buyRes).toMutableList()
        }
        if (res.isEmpty()) return emptyList()

        // Calculate total amount and price
        val totalAmount = res.sumByFloat { it.amount }
        val totalPrice = res.sumBy { it.price }
        res.add(HistoryItem("Totaal", totalAmount, totalPrice, true))

        // Calculate total balance
        val persons = when (selectedPerson) {
            null -> realm.where(Person::class.java).findAll()
            else -> realm.where(Person::class.java).equalTo("id", selectedPerson.id).findAll()
        }
        val peopleWithBalance = persons.filter { it.balance != 0 }.size.toFloat()
        val totalBalance = persons.sumByFloat { it.balance.toFloat() }.toInt()
        res.add(HistoryItem("Balans", peopleWithBalance, totalBalance, false))

        // Calculate difference between total balance and total price
        if (selectedPerson != null && showBuy == SHOW_NETTO) {
            val historyItem: HistoryItem
            val balancePriceDiff = totalBalance - totalPrice
            historyItem = if (balancePriceDiff < 0) {
                HistoryItem("Ontvangen", -1f, -balancePriceDiff, false)
            } else {
                HistoryItem("Overgemaakt", -1f, balancePriceDiff, false)
            }
            res.add(historyItem)
        }

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
            TIMEDIFF_ALWAYS -> cal.add(Calendar.YEAR, multiplier * Calendar.getInstance().get(Calendar.YEAR))
        }
        return cal.time.time
    }


    private fun updateTimePointsText() {
        val view = this.view!!
        val timeFormat = SimpleDateFormat("HH:mm")
        val dateFormat = SimpleDateFormat("dd-MM\nyyyy")


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