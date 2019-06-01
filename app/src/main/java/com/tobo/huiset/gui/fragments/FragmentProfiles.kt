import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.gui.adapters.PersonRecAdapter
import com.tobo.huiset.realmModels.Person
import io.realm.Sort
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tobo.huiset.gui.activies.EditProfileActivity


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
        val persons = realm.where(Person::class.java).sort("balance", Sort.DESCENDING).findAll()

        val rec = view.findViewById<RecyclerView>(R.id.profilesTabRec)
        rec.addItemDecoration(DividerItemDecoration(rec.context, DividerItemDecoration.VERTICAL))
        rec.adapter = PersonRecAdapter(this.context!!, persons,realm, true)
        rec.layoutManager = LinearLayoutManager(this.context)
        val fab = view.findViewById<FloatingActionButton>(R.id.add_profile)

        // hides the fab add_profile when scrolling down
        rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 2)
                    fab.hide()
                else if (dy <= 2)
                    fab.show()
            }
        })

        // opens EditProfileActivity when fab add_profile is clicked
        fab.setOnClickListener {
            val intent = Intent(this.activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

}