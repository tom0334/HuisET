package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Transaction
import com.tobo.huiset.utils.extensions.toTimeAgoString
import com.tobo.huiset.utils.extensions.getBalanceColorString
import com.tobo.huiset.utils.extensions.setTextColorFromHex
import com.tobo.huiset.utils.extensions.toCurrencyString
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
) : RealmRecyclerViewAdapter<Transaction, TransactionRecAdapter.TransactionViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.transaction_rec_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val trans = data?.get(position) ?: return

        val person = trans.getPerson(realmInstance)

        holder.nameTv.text = person?.name
        holder.timeAgo.text = trans.time.toTimeAgoString(includeNewLine = true)
        holder.productTv.text = "${trans.amount} ${trans.getProduct(realmInstance).name}"
        if (trans.isBuy) {
            holder.priceTv.text = "+ ${trans.price.toCurrencyString()} (gekocht)"
            holder.priceTv.setTextColorFromHex((1).getBalanceColorString())
        } else {
            holder.priceTv.text = "${trans.price.toCurrencyString()}"
            holder.priceTv.setTextColor(ContextCompat.getColor(context, R.color.androidStandardTextColor))
        }

        holder.deleteButton.setOnClickListener {
            deleteButtonCallback(trans, person)
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
        val deleteButton = itemView.findViewById<ImageButton>(R.id.main_transactionRec_deleteButton)!!
    }
}