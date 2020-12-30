package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.utils.extensions.toCurrencyString
import com.tobo.huiset.utils.extensions.toFormattedAmount


data class HistoryItem(
    val productName: String,
    val amount: Float,
    val price: Int,
    val total: Boolean
)


class HistoryAdapter(val items: MutableList<HistoryItem>, val context: Context) :
    RecyclerView.Adapter<HistoryViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewholder {
        return HistoryViewholder(
            LayoutInflater.from(context).inflate(R.layout.history_rec_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HistoryViewholder, position: Int) {
        val item = items[position]
        holder.productNameTv.text = item.productName
        holder.priceTv.text = item.price.toCurrencyString()

        if (item.total) {
            holder.totalDivider.visibility = View.VISIBLE
        } else {
            holder.totalDivider.visibility = View.GONE
        }

        val itemAmount = item.amount.toFormattedAmount()
        val layoutParams = holder.itemLayout.layoutParams as RecyclerView.LayoutParams
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        when (holder.productNameTv.text) {
            "Balans" -> {
                holder.amountTv.text = if (itemAmount.toInt() <= 1) "" else "${itemAmount}p"
                setAllTextColors(holder, ContextCompat.getColor(context, R.color.greyHintTextColor))
                if (dpWidth > 900) { // for tablets
                    layoutParams.topMargin = 0
                    layoutParams.bottomMargin = 18
                } else {
                    layoutParams.topMargin = 0
                    layoutParams.bottomMargin = 0
                }
            }
            "Ontvangen", "Overgemaakt" -> {
                holder.amountTv.text = ""
                setAllTextColors(holder, ContextCompat.getColor(context, R.color.greyHintTextColor))
                if (dpWidth > 900) {
                    layoutParams.topMargin = 0
                    layoutParams.bottomMargin = 18
                } else {
                    layoutParams.topMargin = 40
                    layoutParams.bottomMargin = 56
                }
            }
            else -> {
                holder.amountTv.text = itemAmount
                setAllTextColors(holder, holder.defaultRecColor)
                if (dpWidth > 900) {
                    layoutParams.topMargin = 18
                    layoutParams.bottomMargin = 0
                } else {
                    layoutParams.topMargin = 56
                    layoutParams.bottomMargin = 0
                }
            }
        }
    }

    private fun setAllTextColors(
        holder: HistoryViewholder,
        textColor: Int
    ) {
        holder.productNameTv.setTextColor(textColor)
        holder.amountTv.setTextColor(textColor)
        holder.priceTv.setTextColor(textColor)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

}

class HistoryViewholder(view: View) : RecyclerView.ViewHolder(view) {
    val itemLayout = view.findViewById<RelativeLayout>(R.id.historyView_item)!!
    val productNameTv = view.findViewById<TextView>(R.id.historyView_product_name)!!
    val amountTv = view.findViewById<TextView>(R.id.historyView_productAmount)!!
    val priceTv = view.findViewById<TextView>(R.id.historyView_total_price)!!
    val totalDivider = view.findViewById<View>(R.id.historyView_item_divider)!!
    val defaultRecColor: Int = productNameTv.textColors.defaultColor
}
