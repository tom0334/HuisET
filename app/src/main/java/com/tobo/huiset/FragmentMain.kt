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
import com.tobo.huiset.getBeerProduct
import com.tobo.huiset.helpers.ItemClickSupport
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.toPixel
import f.tom.consistentspacingdecoration.ConsistentSpacingDecoration
import io.realm.Sort


class FragmentMain : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transActionRec = setupTransactionsRec(view)
        setupTurfRec(view, transActionRec)
    }

    private fun setupTransactionsRec(view: View) : RecyclerView{
        val transactions = realm.where(Transaction::class.java)
            .sort("time", Sort.DESCENDING)
            .findAll()

        val transActionRec = view.findViewById<RecyclerView>(R.id.recentRecyclerView)
        transActionRec.adapter = TransactionRecAdapter(this.context!!, transactions,realm, true)
        transActionRec.layoutManager = LinearLayoutManager(this.context)
        return transActionRec
    }


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
                    realm.copyToRealm(t)
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
    }


}


