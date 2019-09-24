package com.tobo.huiset.gui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class TransferCalcPersonRecAdapter(
    private val transferMoneyActivity: TransferMoneyActivity,
    val context: Context,
    val realm: Realm,
    data: RealmResults<Person>?,
    autoUpdate: Boolean,
    private val chosenArray: Array<String>
) : RealmRecyclerViewAdapter<Person, TransferCalcPersonRecAdapter.PersonViewHolder>(data, autoUpdate) {

    private val hasPaidMap = mutableMapOf<Person, Int>()
    private var clickedMap = mutableMapOf<Int, Person>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_transfercalc_rec_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = data?.get(position) ?: return

        holder.nameTv.text = "${person.name}"
        holder.balanceTv.text = person.balance.toCurrencyString()
        val colorString = data?.get(position)!!.balance.getBalanceColorString()
        holder.balanceTv.setTextColorFromHex(colorString)

        holder.colorLine.setBackgroundColor(Color.parseColor(person.color))

        if (hasPaidMap.contains(person)) {
            holder.actionTv.text = "heeft ${hasPaidMap[person]!!.toCurrencyString()} overgemaakt naar ${clickedMap[position]!!.name}"
            holder.actionTv.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
        }
        else {
            holder.actionTv.text = "moet ${(-person.balance).toCurrencyString()} overmaken naar ${transferMoneyActivity.db.findRoommateWithMostBalanceWhoIsNotInArray(chosenArray)!!.name}"
            holder.actionTv.setTextColor(ContextCompat.getColor(context, R.color.androidStandardTextColor))
        }

        holder.itemView.setOnClickListener {

            val mostBalancePerson = transferMoneyActivity.db.findRoommateWithMostBalanceWhoIsNotInArray(chosenArray)

            if (!hasPaidMap.contains(person)) {
                val moneyToTransfer = -person.balance
                hasPaidMap[person] = moneyToTransfer
                clickedMap[position] = mostBalancePerson!!

                transferMoneyActivity.someonePaidSomeone(person, mostBalancePerson, moneyToTransfer, false)
            }
            else {
                transferMoneyActivity.someonePaidSomeone(person, mostBalancePerson!!, hasPaidMap[person]!!, true)

                hasPaidMap.remove(person)
                clickedMap.remove(position)
            }

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.MTCpersonRecItem_name)!!
        val actionTv = itemView.findViewById<TextView>(R.id.MTCpersonRecItem_selected)!!
        val balanceTv = itemView.findViewById<TextView>(R.id.MTCpersonRecItem_balance)!!
        val colorLine = itemView.findViewById<View>(R.id.MTCpersonRec_item_color)!!
    }
}