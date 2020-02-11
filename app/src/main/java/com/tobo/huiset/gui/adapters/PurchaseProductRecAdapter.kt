package com.tobo.huiset.gui.adapters

import FragmentPurchases
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows products in a recyclerview. These should be updated automatically when the objects are changed in realm
 */
class PurchaseProductRecAdapter(
    private val fragmentPurchases: FragmentPurchases,
    val realm: Realm,
    data: RealmResults<Product>,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Product, PurchaseProductRecAdapter.ProductViewHolder>(data, autoUpdate) {


    private val amountMap: MutableMap<String, Int> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(fragmentPurchases.context).inflate(R.layout.product_purchase_rec_item, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = data?.get(position) ?: return

        holder.amountTv.text = getFromMap(product.id).toString()
        holder.nameTv.text = product.name
        holder.priceTv.text = if (fragmentPurchases.isDeposit) {
            "${product.price.toCurrencyString()}${when (product.species) {
                Product.CRATEPRODUCT -> " (+ €3,90)"
                Product.BEERPRODUCT -> " (+ €0,10)"
                else -> ""
            }}"
        } else
            product.price.toCurrencyString()

        if (getFromMap(product.id) > 0) {
            holder.amountTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.primaryTextColor))
            holder.nameTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.primaryTextColor))
        }
        else {
            holder.amountTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.androidStandardTextColor))
            holder.nameTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.androidStandardTextColor))
        }

        holder.itemView.setOnClickListener {
            amountMap[product.id] = getFromMap(product.id) + 1
            val deposit = when (product.species) {
                Product.BEERPRODUCT -> 10
                Product.CRATEPRODUCT -> 390
                else -> 0
            }
            fragmentPurchases.increaseCounter(product.price, deposit)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    fun getFromMap(id: String): Int {
        if (!amountMap.containsKey(id)) amountMap[id] = 0
        return amountMap[id]!!
    }

    fun resetMapValues() {
        amountMap.clear()
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.productRecItem_name)!!
        val priceTv = itemView.findViewById<TextView>(R.id.productRecItem_price)!!
        val amountTv = itemView.findViewById<TextView>(R.id.productRecItem_purch_amount)!!
    }
}