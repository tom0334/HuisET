package com.tobo.huiset.gui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    val db by lazy { HuisETDB(realm) }

    interface TurfHandler{
        fun handleSingleTurf(person: Person)

//        fun handleMultiTurf(){}
    }

    var selecting = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurfRecViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.turf_rec_item, parent, false)
        return TurfRecViewHolder(view)
    }

    override fun onBindViewHolder(holder: TurfRecViewHolder, position: Int) {
        val person = data?.get(position) ?: return
        holder.colorView.setBackgroundColor(Color.parseColor(person.color))

        holder.nameTv.text = person.name
        holder.balanceTv.text = person.balance.toCurrencyString()
        holder.balanceTv.setTextColorFromHex(person.balance.getBalanceColorString())

        val bgColor = if(person.isSelectedForMultiPersonTurf){
            Color.parseColor("#ff0000")
        }else{
            Color.parseColor("#ffffff")

        }

        holder.itemView.setOnClickListener {
            if (selecting) {
                db.toggleSelectionInTurfRec(person)

                if(db.getSelectedPersonsInTurfRec().isEmpty()){
                    selecting = false
                }
                notifyItemChanged(position)

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

        val realm = person.realm


        if(selecting){
            db.selectPersonInTurfRec(person)
            notifyItemChanged(position)
        }
        else if(! selecting){
            realm.beginTransaction()
            data?.forEachIndexed { index, p ->
                p.isSelectedForMultiPersonTurf = false
                this.notifyItemChanged(index)
            }
            realm.commitTransaction()
        }
    }





    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class TurfRecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.turfRecItem_name)!!
        val balanceTv = itemView.findViewById<TextView>(R.id.turfRecItem_balance)!!
        val colorView = itemView.findViewById<View>(R.id.turfRecItem_color_line)
    }
}