import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.HistoryAdapter
import com.tobo.huiset.gui.adapters.HistoryItem
import com.tobo.huiset.gui.adapters.HistoryPersonRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.ItemClickSupport
import kotlinx.android.synthetic.main.fragment_products.*


public class FragmentHistory : HuisEtFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val historyRec= view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        historyRec.adapter = HistoryAdapter(findHistoryItems(), this.context!!)
        historyRec.layoutManager = LinearLayoutManager(this.context!!)

        setupPersonRec(view)
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
        }
    }



    private fun findHistoryItems(): MutableList<HistoryItem>{
        val products = realm.where(Product::class.java).findAll()
        return products.map{ HistoryItem(it, 2)}.toMutableList()
    }

    override fun onDestroy() {
        super.onDestroy()
        val rec = view?.findViewById<RecyclerView>(R.id.historyPersonRec)
        if(rec!= null)ItemClickSupport.removeFrom(rec)

    }


}