package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows products in a recyclerview. These should be updated automatically when the objects are changed in realm
 */
class ProductRecAdapter(
    val context: Context,
    data: RealmResults<Product>?,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Product, ProductRecAdapter.ProductViewHolder>(data, autoUpdate) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_rec_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = data?.get(position) ?: return

        holder.nameTv.text = product.name
        holder.priceTv.text = product.price.toCurrencyString()
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.productRecItem_name)!!
        val priceTv = itemView.findViewById<TextView>(R.id.productRecItem_price)!!
    }
}