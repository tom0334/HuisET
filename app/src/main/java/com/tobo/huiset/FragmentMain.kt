import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.adapters.TransactionRecAdapter
import com.tobo.huiset.adapters.TurfRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction


class FragmentMain : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTransactionsRec(view)
        setupTurfRec(view)
    }

    private fun setupTransactionsRec(view: View){
        val transactions = realm.where(Transaction::class.java).findAll()

        val transActionRec = view.findViewById<RecyclerView>(R.id.recentRecyclerView)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions,realm, true)
        transActionRec.layoutManager = LinearLayoutManager(this.context)
    }


    private fun setupTurfRec(view: View) {
        val profiles = realm.where(Person::class.java).findAll()

        val turfRec = view.findViewById<RecyclerView>(R.id.mainPersonRec)
        turfRec.adapter = TurfRecAdapter(this.context!!, profiles, realm, true)
        turfRec.layoutManager = GridLayoutManager(this.context, 2)

    }


}