import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.activities.EditProfileActivity
import com.tobo.huiset.gui.adapters.PersonRecAdapter
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.ItemClickSupport
import io.realm.Sort


/**
 * Shows the profiles list.
 */
class FragmentProfiles : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profiles, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()

        // delete the ItemClickSupport
        val rec = view?.findViewById<RecyclerView>(R.id.profilesTabRec)
        if (rec != null) {
            ItemClickSupport.removeFrom(rec)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //this sets up the recyclerview to show the persons
        val persons = db.findAllCurrentPersons()

        val rec = view.findViewById<RecyclerView>(R.id.profilesTabRec)
        rec.addItemDecoration(DividerItemDecoration(rec.context, DividerItemDecoration.VERTICAL))
        rec.adapter = PersonRecAdapter(this.context!!, realm, persons, true)
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

        // opens EditProfileActivity on the correct profile if a profile is clicked
        ItemClickSupport.addTo(rec).setOnItemClickListener { _, position, _ ->
            val person = persons[position]
            val intent = Intent(this.activity, EditProfileActivity::class.java)
                .putExtra("PERSON_ID", person?.id)
            startActivity(intent)
        }
    }

}