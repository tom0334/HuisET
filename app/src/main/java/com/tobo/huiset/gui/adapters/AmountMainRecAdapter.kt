package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R


class AmountMainRecAdapter(val items: List<Int>, val context: Context) :
    RecyclerView.Adapter<AmountMainViewHolder>() {

    var selectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmountMainViewHolder {
        return AmountMainViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.amount_main_rec_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AmountMainViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            this.notifyItemChanged(selectedPos) // old pos
            this.notifyItemChanged(position) // new pos
            this.selectedPos = position
        }

        val colorResId =
            if (position == selectedPos) R.color.secondaryDarkColor else R.color.primaryDarkColor
        holder.cardV.setBackgroundColor(ContextCompat.getColor(context, colorResId))

        holder.amountTv.text = items[position].toString()
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    fun getSelectedAmount(): Int {
        return items[selectedPos]
    }

    fun resetAmountToFirst() {
        this.notifyItemChanged(selectedPos)
        selectedPos = 0
        this.notifyItemChanged(selectedPos)

    }
}

class AmountMainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardV: CardView = itemView.findViewById(R.id.amountMainRecItem)
    val amountTv: TextView = view.findViewById(R.id.amountMainRecItem_name)
}