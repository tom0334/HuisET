package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.extensions.setTextColorFromHex
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class PersonRecAdapter(
    val context: Context,
    data: RealmResults<Person>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Person, PersonRecAdapter.PersonViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_rec_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = data?.get(position) ?: return

        holder.nameTv.text = person.name
        holder.balanceTv.text = person.balance.toCurrencyString()
        val colorString = data?.get(position)!!.balanceColor
        holder.balanceTv.setTextColorFromHex(colorString)
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.personRecItem_name)!!
        val balanceTv = itemView.findViewById<TextView>(R.id.personRecItem_balance)!!
    }
}