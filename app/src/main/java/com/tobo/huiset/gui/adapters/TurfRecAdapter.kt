package com.tobo.huiset.gui.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.utils.HuisETDB
import com.tobo.huiset.utils.extensions.getBalanceColorString
import com.tobo.huiset.utils.extensions.setTextColorFromHex
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class TurfRecAdapter(
    val context: Context,
    data: RealmResults<Person>?,
    autoUpdate: Boolean,
    val realm: Realm,
    val callback: TurfHandler
) :
    RealmRecyclerViewAdapter<Person, TurfRecAdapter.TurfRecViewHolder>(data, autoUpdate) {

    interface TurfHandler{
        fun handleSingleTurf(person: Person)
        fun onSelectionChanged(selecting:Boolean)
    }




    val db by lazy { HuisETDB(realm) }

    val selectedPersonIds = mutableSetOf<String>()

    private fun personIsSelected(person:Person) = selectedPersonIds.contains(person.id)


    var selecting = false
    set(value) {
        field = value
        callback.onSelectionChanged(selecting)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurfRecViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.turf_rec_item, parent, false)
        return TurfRecViewHolder(view)
    }

    override fun onBindViewHolder(holder: TurfRecViewHolder, position: Int) {
        val person = data?.get(position) ?: return
        holder.colorView.setBackgroundColor(Color.parseColor(person.color))

        if (person.isHuisRekening) {
            holder.iconIv.visibility = View.VISIBLE
        } else {
            holder.iconIv.visibility = View.GONE
        }

        holder.nameTv.text = person.name
        holder.balanceTv.text = person.balance.toCurrencyString()
        holder.balanceTv.setTextColorFromHex(person.balance.getBalanceColorString())

        val bgColor = if(personIsSelected(person)){
            ContextCompat.getColor(context,R.color.secondaryLightColor)
        }else{
            Color.parseColor("#ffffff")

        }

        holder.itemView.setOnClickListener {
            if (selecting) {
                val reversed = ! personIsSelected(person)
                setPersonSelected(person,position,reversed)

                if(selectedPersonIds.isEmpty()){
                    selecting = false
                }

            } else {
                callback.handleSingleTurf(person)
            }
        }


        holder.itemView.setOnLongClickListener {
            onLongPress(person,position)
            return@setOnLongClickListener true
        }


        holder.itemView.setBackgroundColor(bgColor)
    }


    fun onLongPress(person: Person, position: Int){
        selecting = ! selecting

        if(selecting){
            setPersonSelected(person,position,true)
        }
        else if(! selecting){
            data?.forEachIndexed { index, p ->
                setPersonSelected(person,index,false)
            }
        }
    }

    private fun setPersonSelected(person: Person, index: Int, selected:Boolean) {
        if(selected){
            selectedPersonIds.add(person.id)
        }else{
            selectedPersonIds.remove(person.id)
        }
        this.notifyItemChanged(index)
    }



    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    fun saveState(outState:Bundle) {
        outState.putStringArrayList("Selected",ArrayList(selectedPersonIds.toList()))
        outState.putBoolean("Selecting",selecting)
    }

    fun restoreState(savedInstanceState:Bundle?){
        if(savedInstanceState == null) return
        val saved = savedInstanceState.getStringArrayList("Selected") ?: return
        this.selectedPersonIds.addAll(saved)

        if(saved.size > 0){
            this.notifyDataSetChanged()
        }
        this.selecting = savedInstanceState.getBoolean("Selecting")

    }

    class TurfRecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv = itemView.findViewById<ImageView>(R.id.turfRecItem_icon)!!
        val nameTv = itemView.findViewById<TextView>(R.id.turfRecItem_name)!!
        val balanceTv = itemView.findViewById<TextView>(R.id.turfRecItem_balance)!!
        val colorView = itemView.findViewById<View>(R.id.turfRecItem_color_line)
    }
}