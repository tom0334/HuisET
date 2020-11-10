package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import io.realm.Realm


class HistoryPersonRecAdapter(
    val items: MutableList<Person?>,
    val context: Context,
    val realm: Realm
) :
    RecyclerView.Adapter<HistoryPersonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryPersonViewHolder {
        return HistoryPersonViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.history_person_rec_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryPersonViewHolder, position: Int) {
        val p = items[position]

        fun setColors(selected: Boolean) {
            if (selected) {
                holder.rootV.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.secondaryDarkColor
                    )
                )
            } else {
                holder.rootV.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primaryDarkColor
                    )
                )
            }
        }

        if (p == null) {
            holder.personNameTv.text = "Totaal"
            val selected =
                realm.where(Person::class.java).equalTo("selectedInHistoryView", true).count() == 0L
            setColors(selected)
        } else {
            holder.personNameTv.text = p.name
            setColors(p.isSelectedInHistoryView)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class HistoryPersonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val personNameTv = view.findViewById<TextView>(R.id.historyPersonRec_name)!!
    val rootV = view.findViewById<View>(R.id.historyPersonRec_rootView)!!
}