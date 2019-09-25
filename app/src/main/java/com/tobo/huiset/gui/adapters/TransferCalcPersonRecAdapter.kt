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
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Person, TransferCalcPersonRecAdapter.PersonViewHolder>(data, autoUpdate) {

    private var hasPaidMap: MutableMap<Person, Int> = mutableMapOf()
    private var personMatchMap: MutableMap<Person, Person> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_transfercalc_rec_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = data?.get(position) ?: return

        holder.nameTv.text = person.name
        holder.balanceTv.text = person.balance.toCurrencyString()
        val colorString = data?.get(position)!!.balance.getBalanceColorString()
        holder.balanceTv.setTextColorFromHex(colorString)

        holder.colorLine.setBackgroundColor(Color.parseColor(person.color))

        val theoreticalBalanceList = transferMoneyActivity.theoreticalBalanceList.toList().sortedByDescending { it.second }.toMutableList()

        if (hasPaidMap.contains(person)) {
            holder.actionTv.text = "heeft ${hasPaidMap[person]!!.toCurrencyString()} overgemaakt naar ${personMatchMap[person]!!.name}"
            holder.actionTv.setTextColor(ContextCompat.getColor(context, R.color.primaryTextColor))
        }
        else {
            val mostBalancePerson = theoreticalBalanceList.first().first
            personMatchMap[person] = mostBalancePerson
            val otherPerson = personMatchMap[person]

            holder.actionTv.text = "moet ${(-person.balance).toCurrencyString()} overmaken naar ${otherPerson!!.name}"
            holder.actionTv.setTextColor(ContextCompat.getColor(context, R.color.androidStandardTextColor))

            theoreticalBalanceList.remove(Pair(otherPerson, otherPerson.balance))
            theoreticalBalanceList.add(Pair(otherPerson, otherPerson.balance + person.balance))
        }

        transferMoneyActivity.theoreticalBalanceList = theoreticalBalanceList

        holder.itemView.setOnClickListener {
            val otherPerson = personMatchMap[person]!!

            if (!hasPaidMap.contains(person)) {
                val moneyToTransfer = -person.balance
                hasPaidMap[person] = moneyToTransfer

                transferMoneyActivity.someonePaidSomeone(person, otherPerson, moneyToTransfer, false)
            }
            else {
                val otherPersonUndo = personMatchMap[person]!!
                transferMoneyActivity.someonePaidSomeone(person, otherPersonUndo, hasPaidMap[person]!!, true)

                theoreticalBalanceList.remove(Pair(otherPersonUndo, otherPersonUndo.balance - hasPaidMap[person]!!))
                theoreticalBalanceList.add(Pair(otherPersonUndo, otherPersonUndo.balance))

                hasPaidMap.remove(person)
                transferMoneyActivity.theoreticalBalanceList = theoreticalBalanceList
            }

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