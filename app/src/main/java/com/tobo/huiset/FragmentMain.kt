import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tobo.huiset.HuisEtFragment
import com.tobo.huiset.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.adapters.TransactionRecAdapter
import com.tobo.huiset.realmModels.Transaction


public class FragmentMain : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactions = realm.where(Transaction::class.java).findAll()

        val transActionRec = view.findViewById<RecyclerView>(R.id.recentRecyclerView)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions,realm, true)
        transActionRec.layoutManager = LinearLayoutManager(this.context)
    }

}