package com.tobo.huiset.gui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.gui.activities.TransferMoneyActivity
import com.tobo.huiset.realmModels.Person
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
class TransferPersonRecAdapter(
    private val transferMoneyActivity: TransferMoneyActivity,
    val context: Context,
    val realm: Realm,
    data: RealmResults<Person>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Person, TransferPersonRecAdapter.PersonViewHolder>(data, autoUpdate) {

    val chosenMap = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.person_transfer_rec_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = data?.get(position) ?: return

        if (person.isHuisRekening) {
            holder.iconIv.visibility = View.VISIBLE
        } else {
            holder.iconIv.visibility = View.GONE
        }

        holder.nameTv.text = person.name
        holder.balanceTv.text = person.balance.toCurrencyString()
        val colorString = data?.get(position)!!.balance.getBalanceColorString()
        holder.balanceTv.setTextColorFromHex(colorString)

        holder.colorLine.setBackgroundColor(Color.parseColor(person.color))

        holder.selectedTv.text = "Geselecteerd"

        if (chosenMap.contains(person.id)) {
            holder.nameTv.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
            holder.selectedTv.visibility = View.VISIBLE
            holder.checkedIv.setColorFilter(
                ContextCompat.getColor(
                    transferMoneyActivity,
                    R.color.primaryTextColor
                )
            )
        } else {
            holder.nameTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.androidStandardTextColor
                )
            )
            holder.selectedTv.visibility = View.GONE
            holder.checkedIv.setColorFilter(
                ContextCompat.getColor(
                    transferMoneyActivity,
                    R.color.grey
                )
            )
        }

        holder.itemView.setOnClickListener {
            if (!chosenMap.contains(person.id)) {
                chosenMap.add(person.id)
                transferMoneyActivity.increaseSelectedPersonsCounter(true)
            } else {
                chosenMap.remove(person.id)
                transferMoneyActivity.increaseSelectedPersonsCounter(false)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv = itemView.findViewById<ImageView>(R.id.MTpersonRec_item_icon)!!
        val nameTv = itemView.findViewById<TextView>(R.id.MTpersonRecItem_name)!!
        val selectedTv = itemView.findViewById<TextView>(R.id.MTpersonRecItem_selected)!!
        val balanceTv = itemView.findViewById<TextView>(R.id.MTpersonRecItem_balance)!!
        val checkedIv = itemView.findViewById<ImageView>(R.id.MTcheckedImage)!!
        val colorLine = itemView.findViewById<View>(R.id.MTpersonRec_item_color)!!
    }
}