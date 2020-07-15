package com.tobo.huiset.gui.adapters

import FragmentPurchases
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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


    /**
     * The different view types for this recyclerview.k
     *
     * It has 2 types: a normal product and a new product button
     */
    companion object{
        private const val VIEW_TYPE_PRODUCT = 1
        private const val VIEW_TYPE_NEW_PRODUCT_BUTTON = 2
        private const val VIEW_TYPE_TEMP_PRODUCT = 3
    }

    override fun getItemViewType(position: Int):Int = when(position ){
        0 -> VIEW_TYPE_TEMP_PRODUCT
        data?.lastIndex?.plus(2) -> VIEW_TYPE_NEW_PRODUCT_BUTTON
        else -> VIEW_TYPE_PRODUCT
    }


    //Its a java hashmap instead of a kotlin map because the java one is serialisable.
    private var amountMap: HashMap<String, Int> = HashMap()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutResId = when(viewType){
            VIEW_TYPE_PRODUCT -> R.layout.product_purchase_rec_item
            VIEW_TYPE_NEW_PRODUCT_BUTTON -> R.layout.product_purchase_rec_item_new_product
            VIEW_TYPE_TEMP_PRODUCT -> R.layout.product_purchase_rec_item_custom_temp
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater.from(fragmentPurchases.context).inflate(layoutResId, parent, false)

        return when(viewType){
            VIEW_TYPE_PRODUCT -> ProductViewHolder(view)
            VIEW_TYPE_NEW_PRODUCT_BUTTON -> NewProductRecViewHolder(view)
            VIEW_TYPE_TEMP_PRODUCT -> TempProductRecViewHolder(view)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0){
            bindTempProductButton(holder as TempProductRecViewHolder)
        }
        //if it is the last item(plus 2 because of the
        else if(data!= null && position == data!!.lastIndex + 2){
            bindNewProductButton(holder as NewProductRecViewHolder)

        }else{
            //minus one because of the first item is custom
            val product = data?.get(position-1)
            if(product != null){
                bindProduct(holder as ProductViewHolder,product, position)
            }
        }
    }

    private fun bindTempProductButton(holder: TempProductRecViewHolder) {
        holder.itemView.setOnClickListener {
            fragmentPurchases.startCustomTurf()
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
        holder.priceTv.text = product.price.toCurrencyString()

        if (getFromMap(product.id) > 0) {
            holder.amountTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.primaryTextColor))
            holder.nameTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.primaryTextColor))
        }
        else {
            holder.amountTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.androidStandardTextColor))
            holder.nameTv.setTextColor(ContextCompat.getColor(fragmentPurchases.context!!, R.color.androidStandardTextColor))
        }

        holder.itemView.setOnClickListener {
            if (fragmentPurchases.decreasing) {
                if (amountMap[product.id] == 0) {
                    Toast.makeText(fragmentPurchases.context, "Mag niet negatief zijn", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else
                    amountMap[product.id] = getFromMap(product.id) - 1
                fragmentPurchases.increaseCounter(-product.price)
            }
            else {
                amountMap[product.id] = getFromMap(product.id) + 1
                fragmentPurchases.increaseCounter(product.price)
            }
            notifyItemChanged(position)

            val decFAB: FloatingActionButton = fragmentPurchases.view!!.findViewById(R.id.decreaseFAB)

            val empty = amountMap.all { it.value == 0 }
            if (empty) {
                fragmentPurchases.decreasing = false
                decFAB.hide()
            }
            else {
                decFAB.show()
            }
        }
    }



    //THE NEW PRODUCT BUTTON and the custom temp purchase are also buttons
    override fun getItemCount(): Int {
        return if (data == null) 2 else data!!.size + 2
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
    class TempProductRecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}

