package com.tobo.huiset.gui.adapters

import FragmentPurchases
import android.annotation.SuppressLint
import android.os.Bundle
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
import java.lang.IllegalArgumentException
import java.util.HashMap

/**
 * Shows products in a recyclerview. These should be updated automatically when the objects are changed in realm
 */
class PurchaseProductRecAdapter(
    private val fragmentPurchases: FragmentPurchases,
    val realm: Realm,
    data: RealmResults<Product>,
    autoUpdate: Boolean
) : RealmRecyclerViewAdapter<Product, RecyclerView.ViewHolder>(data, autoUpdate) {


    //Its a java hashmap instead of a kotlin map because the java one is serialisable.
    private var amountMap: HashMap<String, Int> = HashMap()

    /**
     * The different view types for this recyclerview.
     *
     * It has 2 types: a normal product and a new product button
     */
    companion object{
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_NEW_PRODUCT_BUTTON = 2
    }

//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        val product = data?.get(position) ?: return
//    }

    override fun getItemViewType(position: Int):Int = when(position ){
        data?.size -> VIEW_TYPE_NEW_PRODUCT_BUTTON
        else -> VIEW_TYPE_PRODUCT
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutResId = when(viewType){
            VIEW_TYPE_PRODUCT -> R.layout.product_purchase_rec_item
            VIEW_TYPE_NEW_PRODUCT_BUTTON -> R.layout.product_purchase_rec_item_new_product
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater.from(fragmentPurchases.context).inflate(layoutResId, parent, false)

        return when(viewType){
            VIEW_TYPE_PRODUCT -> ProductViewHolder(view)
            VIEW_TYPE_NEW_PRODUCT_BUTTON -> NewProductRecViewHolder(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == data?.size){
            bindNewProductButton(holder as NewProductRecViewHolder)

        }else{
            val product = data?.get(position)
            if(product != null){
                bindProduct(holder as ProductViewHolder,product, position)
            }
        }
    }

    private fun bindNewProductButton(holder: NewProductRecViewHolder) {
        holder.itemView.setOnClickListener {
            fragmentPurchases.onCreateNewProductClicked()
        }
    }

    private fun bindProduct(holder:ProductViewHolder , product: Product, position: Int) {
        holder.amountTv.text = getFromMap(product.id).toString()
        holder.nameTv.text = product.name
        holder.priceTv.text = if (fragmentPurchases.depositEnabled) {
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



    //THE NEW PRODUCT BUTTON IS ALSO AN ITEM
    override fun getItemCount(): Int {
        return if (data == null) 1 else data!!.size + 1
    }

    fun getFromMap(id: String): Int {
        if (!amountMap.containsKey(id)) amountMap[id] = 0
        return amountMap[id]!!
    }

    fun resetMapValues() {
        amountMap.clear()
        notifyDataSetChanged()
    }

    fun saveOutState(outState: Bundle) {
        outState.putSerializable("amountMap",amountMap)
    }

    fun restoreInstanceState(outState: Bundle){
        amountMap = outState.getSerializable("amountMap") as HashMap<String, Int>
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.productRecItem_name)!!
        val priceTv = itemView.findViewById<TextView>(R.id.productRecItem_price)!!
        val amountTv = itemView.findViewById<TextView>(R.id.productRecItem_purch_amount)!!
    }

    class NewProductRecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}