package com.tobo.huiset.gui.adapters
import io.realm.Realm
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class HistoryPersonRecAdapter(
    val context: Context,
    data: RealmResults<Person>?,
    autoUpdate: Boolean,
    val realm: Realm
) : RealmRecyclerViewAdapter<Person, HistoryPersonViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryPersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.history_person_rec_item, parent, false)
        return HistoryPersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryPersonViewHolder, position: Int) {
        val p = data?.get(position) ?: return

        fun setColors(selected: Boolean) {
            if (selected) {
                holder.rootV.setBackgroundColor(ContextCompat.getColor(context, R.color.secondaryDarkColor))
            } else {
                holder.rootV.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryDarkColor))
            }
        }

        if (p == null) {
            holder.personNameTv.text = "Totaal"
            val selected = realm.where(Person::class.java).equalTo("selectedInHistoryView", true).count() == 0L
            setColors(selected)
        } else {
            holder.personNameTv.text = p.name
            setColors(p.isSelectedInHistoryView)
        }
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }


}

class HistoryPersonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val personNameTv = view.findViewById<TextView>(R.id.historyPersonRec_name)!!
    val rootV = view.findViewById<View>(R.id.historyPersonRec_rootView)!!
}


