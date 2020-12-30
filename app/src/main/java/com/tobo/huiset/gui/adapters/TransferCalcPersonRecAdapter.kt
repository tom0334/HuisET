package com.tobo.huiset.gui.adapters

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
import java.lang.Math.abs

/**
 * Shows persons in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class TransferCalcPersonRecAdapter(
    private val transferMoneyActivity: TransferMoneyActivity,
    val realm: Realm,
    data: RealmResults<Person>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Person, TransferCalcPersonRecAdapter.PersonViewHolder>(
    data,
    autoUpdate
) {

    private var hasPaidMap: MutableMap<Person, Int> = mutableMapOf()
    private var personMatchMap: MutableMap<Person, Person> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(transferMoneyActivity)
            .inflate(R.layout.person_transfercalc_rec_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = data?.get(position) ?: return

        holder.nameTv.text = person.name
        holder.colorLine.setBackgroundColor(Color.parseColor(person.color))

        val theoreticalBalanceList =
            transferMoneyActivity.theoreticalBalanceList.toList().sortedByDescending { it.second }
                .toMutableList()
        var ownTheoreticalBalance = person.balance

        if (hasPaidMap.contains(person)) {
            if (hasPaidMap[person]!! > 0) {
                holder.actionTv.text =
                    "heeft ${hasPaidMap[person]!!.toCurrencyString()} overgemaakt naar ${personMatchMap[person]!!.name}"
                holder.checkedIv.setColorFilter(
                    ContextCompat.getColor(
                        transferMoneyActivity,
                        R.color.primaryTextColor
                    )
                )
            } else {
                holder.actionTv.text =
                    "heeft ${(-hasPaidMap[person]!!).toCurrencyString()} ontvangen van ${personMatchMap[person]!!.name}"
                holder.checkedIv.setColorFilter(
                    ContextCompat.getColor(
                        transferMoneyActivity,
                        R.color.primaryTextColor
                    )
                )
            }
            holder.actionTv.setTextColor(
                ContextCompat.getColor(
                    transferMoneyActivity,
                    R.color.primaryTextColor
                )
            )
            ownTheoreticalBalance += hasPaidMap[person]!!
        } else {
            val neededPerson = if (person.balance < 0) theoreticalBalanceList.first().first
            else theoreticalBalanceList.last().first
            personMatchMap[person] = neededPerson
            val otherPerson = personMatchMap[person]

            if (person.balance < 0) {
                holder.actionTv.text =
                    "moet ${(-person.balance).toCurrencyString()} overmaken naar ${otherPerson!!.name}"
                holder.checkedIv.setColorFilter(
                    ContextCompat.getColor(
                        transferMoneyActivity,
                        R.color.grey
                    )
                )
            } else {
                holder.actionTv.text =
                    "moet ${person.balance.toCurrencyString()} ontvangen van ${otherPerson!!.name}"
                holder.checkedIv.setColorFilter(
                    ContextCompat.getColor(
                        transferMoneyActivity,
                        R.color.grey
                    )
                )
            }
            holder.actionTv.setTextColor(
                ContextCompat.getColor(
                    transferMoneyActivity,
                    R.color.androidStandardTextColor
                )
            )

            theoreticalBalanceList.remove(Pair(otherPerson, otherPerson.balance))
            if (person.balance < 0) {
                theoreticalBalanceList.add(Pair(otherPerson, otherPerson.balance + person.balance))
            } else {
                theoreticalBalanceList.add(Pair(otherPerson, otherPerson.balance - person.balance))
            }
        }

        holder.balanceTv.text = ownTheoreticalBalance.toCurrencyString()
        val colorString = ownTheoreticalBalance.getBalanceColorString()
        holder.balanceTv.setTextColorFromHex(colorString)


        transferMoneyActivity.theoreticalBalanceList = theoreticalBalanceList

        holder.itemView.setOnClickListener {
            val otherPerson = personMatchMap[person]!!

            if (!hasPaidMap.contains(person)) {
                val moneyToTransfer = -person.balance
                hasPaidMap[person] = moneyToTransfer
                transferMoneyActivity.someonePaidSomeone(
                    person,
                    otherPerson,
                    abs(moneyToTransfer))

                notifyItemChanged(position)
            } else {
                val otherPersonUndo = personMatchMap[person]!!
                transferMoneyActivity.undoSomeonePaid(person,hasPaidMap[person]!!)

                if (hasPaidMap[person]!! > 0) {
                    theoreticalBalanceList.remove(
                        Pair(
                            otherPersonUndo,
                            otherPersonUndo.balance - hasPaidMap[person]!!
                        )
                    )
                } else {
                    theoreticalBalanceList.remove(
                        Pair(
                            otherPersonUndo,
                            otherPersonUndo.balance + hasPaidMap[person]!!
                        )
                    )
                }
                theoreticalBalanceList.add(Pair(otherPersonUndo, otherPersonUndo.balance))

                hasPaidMap.remove(person)
                transferMoneyActivity.theoreticalBalanceList = theoreticalBalanceList

                notifyItemChanged(position)
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
        val checkedIv = itemView.findViewById<ImageView>(R.id.MTCcheckedImage)!!
        val colorLine = itemView.findViewById<View>(R.id.MTCpersonRec_item_color)!!
    }
}