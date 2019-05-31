import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.HuisEtActivity
import com.tobo.huiset.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.adapters.PersonRecAdapter
import com.tobo.huiset.realmModels.Person
import io.realm.Realm
import java.util.*


/**
 * Shows the profiles list.
 */
class FragmentProfiles : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profiles, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //this sets up the recyclerview to show the persons
        val persons = realm.where(Person::class.java).findAll()
        val rec = view.findViewById<RecyclerView>(R.id.profilesTabRec)
        rec.adapter = PersonRecAdapter(this.context!!, persons,realm, true)
        rec.layoutManager = LinearLayoutManager(this.context)
    }

}