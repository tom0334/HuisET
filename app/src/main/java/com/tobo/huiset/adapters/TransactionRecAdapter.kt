package com.tobo.huiset.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Transaction
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows Transactions in a recyclerview. These should be updated automatically when the objects are changed in realm
 *
 */
class TransactionRecAdapter(val context: Context, data: RealmResults<Transaction>?, val realmInstance: Realm, autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<Transaction, TransactionRecAdapter.TransactionViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.transaction_rec_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.nameTv.text = data?.get(position)?.person?.name
        holder.productTv.text = data?.get(position)?.product?.name
        holder.timeAgo.text = data?.get(position)?.getTimeString()
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTv = itemView.findViewById<TextView>(R.id.main_transactionRec_name)
        val productTv = itemView.findViewById<TextView>(R.id.main_transactionRec_productName)
        val timeAgo = itemView.findViewById<TextView>(R.id.main_transactionRec_timeSince)

    }
}