package com.tobo.huiset.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class PersonRecAdapter(val context: Context, data: RealmResults<Person>?, val realmInstance: Realm, autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<Person, PersonRecAdapter.PersonViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_rec_item, parent, false)

        println(data?.get(0)!!.name!!)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.nameTv.text = data?.get(position)?.name
        holder.balanceTv.text = data?.get(position)?.getBalance().toString()
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTv = itemView.findViewById<TextView>(R.id.personRecItem_name)
        val balanceTv = itemView.findViewById<TextView>(R.id.personRecItem_balance)
    }
}