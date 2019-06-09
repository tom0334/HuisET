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
import com.tobo.huiset.realmModels.Product


public class FragmentHistory : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rec= view.findViewById<RecyclerView>(R.id.historyRecyclerView)
        rec.adapter = HistoryAdapter(findHistoryItems(), this.context!!)
        rec.layoutManager = LinearLayoutManager(this.context!!)
    }


    private fun findHistoryItems(): MutableList<HistoryItem>{
        val products = realm.where(Product::class.java).findAll()
        return products.map{ HistoryItem(it, 2)}.toMutableList()
    }


}