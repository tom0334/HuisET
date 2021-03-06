package com.tobo.huiset.gui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.*
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows Transactions in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class TransactionRecAdapter(
    val context: Context,
    data: RealmResults<Transaction>?,
    val amountRec: RecyclerView,
    private val realmInstance: Realm,
    autoUpdate: Boolean,
    val deleteButtonCallback: (Transaction, Person) -> Unit
) : RealmRecyclerViewAdapter<Transaction, TransactionRecAdapter.TransactionViewHolder>(
    data,
    autoUpdate
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.transaction_rec_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val trans = data?.get(position) ?: return
        val person = trans.getPerson(realmInstance, trans.personId)

        holder.nameTv.text = person?.name
        holder.timeAgo.text = trans.time.toTimeAgoString(includeNewLine = true)
        if (trans.otherPersonId != null) {
            holder.productTv.text =
                "Aan ${trans.getPerson(realmInstance, trans.otherPersonId).name}"
        } else if (trans.productId == null) {
            //it's a custom turf
            holder.productTv.text = trans.messageOrProductName
        } else {
            holder.productTv.text =
                "${trans.amount.toFormattedAmount()} ${trans.messageOrProductName}"
        }

        if (trans.isBuy) {
            holder.priceTv.text = "+ ${trans.price.toCurrencyString()} (betaald)"
            holder.priceTv.setTextColorFromHex((1).getBalanceColorString())
        } else {
            holder.priceTv.text = "${trans.price.toCurrencyString()}"
            holder.priceTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.androidStandardTextColor
                )
            )
        }

        holder.deleteButton.setOnClickListener {
            deleteButtonCallback(trans, person)
        }


        //The code below handles the custom turfs. It dynamically adds views to this recyclerviewItem
        //This could also be done with a nested recyclerview, but since these custom turfs are
        //relatively rare, i think this would be better for performance

        //important: Don't call removeAllViews, as that calls requestLayout and invalidate!
        //removeallviewsINLAYOUT is different, and doesn't do that.
        holder.sideEffectContainer.removeAllViewsInLayout()

        if (trans.sideEffects.size > 0) {
            holder.sideEffectContainerTitle.visibility = View.VISIBLE
            holder.sideEffectContainer.visibility = View.VISIBLE
            holder.divider.visibility = View.VISIBLE

            //Add each sideEffect to this view
            trans.sideEffects.forEach {
                val inflatedView = LayoutInflater.from(context)
                    .inflate(R.layout.transaction_rec_sideeffect_item, null)
                inflatedView.findViewById<TextView>(R.id.personName).text =
                    it.getPerson(realmInstance).name
                inflatedView.findViewById<TextView>(R.id.amount).apply {
                    text = it.balanceImpact.toCurrencyString()
                    setTextColor(Color.parseColor(it.balanceImpact.getBalanceColorString()))
                }
                holder.sideEffectContainer.addView(inflatedView)
            }
        } else {
            holder.sideEffectContainerTitle.visibility = View.GONE
            holder.sideEffectContainer.visibility = View.GONE
            holder.divider.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.main_transactionRec_name)!!
        val priceTv = itemView.findViewById<TextView>(R.id.main_transactionRec_price)!!
        val productTv = itemView.findViewById<TextView>(R.id.main_transactionRec_productName)!!
        val timeAgo = itemView.findViewById<TextView>(R.id.main_transactionRec_timeSince)!!
        val deleteButton =
            itemView.findViewById<ImageButton>(R.id.main_transactionRec_deleteButton)!!
        val sideEffectContainer = itemView.findViewById<LinearLayout>(R.id.sideEffectsContainer)
        val sideEffectContainerTitle = itemView.findViewById<TextView>(R.id.paidForTitle)
        val divider = itemView.findViewById<View>(R.id.transCustomDivider)
    }
}