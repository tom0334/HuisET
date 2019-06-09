package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Product

data class HistoryItem(val product:Product,val amount: Int)


class HistoryAdapter(var items : List<HistoryItem>, val context: Context) : RecyclerView.Adapter<HistoryViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewholder {
        return HistoryViewholder(LayoutInflater.from(context).inflate(R.layout.history_rec_item, parent, false))
    }

    override fun onBindViewHolder(holder: HistoryViewholder, position: Int) {
        val item = items[position]
        holder.amountTv.text = item.amount.toString()
        holder.productNameTv.text = item.product.name
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class HistoryViewholder (view: View) : RecyclerView.ViewHolder(view) {
    val productNameTv = view.findViewById<TextView>(R.id.historyView_product_name)
    val amountTv = view.findViewById<TextView>(R.id.historyView_productAmount)
}