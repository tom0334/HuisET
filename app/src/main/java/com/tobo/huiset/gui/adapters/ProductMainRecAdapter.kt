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
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows products in a recyclerview. These should be updated automatically when the objects are changed in realm
 */
class ProductMainRecAdapter(
    val context: Context,
    data: RealmResults<Product>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Product, ProductMainRecAdapter.ProductMainViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductMainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_main_rec_item, parent, false)
        return ProductMainViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductMainViewHolder, position: Int) {
        val product = data?.get(position) ?: return

        holder.nameTv.text = product.name
        holder.priceTv.text = product.price.toCurrencyString()

        val colorResId = if (product.isSelected) R.color.secondaryDarkColor else R.color.primaryDarkColor
        holder.card.setBackgroundColor(ContextCompat.getColor(context, colorResId))
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class ProductMainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.productMainRecItem)
        val nameTv: TextView = itemView.findViewById(R.id.productMainRecItem_name)
        val priceTv: TextView = itemView.findViewById(R.id.productMainRecItem_price)
    }
}