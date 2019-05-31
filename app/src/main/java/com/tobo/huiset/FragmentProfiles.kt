import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.HuisEtFragment
import com.tobo.huiset.R
import com.tobo.huiset.adapters.PersonRecAdapter
import com.tobo.huiset.realmModels.Person
import io.realm.Sort
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tobo.huiset.EditProfileActivity


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
        rec.adapter = PersonRecAdapter(this.context!!, persons,realm, true)
        rec.layoutManager = LinearLayoutManager(this.context)
        val fab = view.findViewById<FloatingActionButton>(R.id.add_profile)
        rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    fab.hide()
                else if (dy < 0)
                    fab.show()
            }
        })

        fab.setOnClickListener {
            Toast.makeText(this.activity, "add profile screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

}