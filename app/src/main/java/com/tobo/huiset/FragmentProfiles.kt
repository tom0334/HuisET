import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.adapters.PersonRecAdapter
import com.tobo.huiset.realmModels.Person
import io.realm.Realm
import io.realm.Sort
import java.util.*


class FragmentProfiles : Fragment() {


    lateinit var realm: Realm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profiles, container, false)
        realm = Realm.getDefaultInstance()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val persons = realm.where(Person::class.java).sort("balance", Sort.DESCENDING).findAll()
        val rec = view.findViewById<RecyclerView>(R.id.profilesTabRec)
        rec.adapter = PersonRecAdapter(this.context!!, persons,realm, true)
        rec.layoutManager = LinearLayoutManager(this.context)
    }

}