package com.tobo.huiset.gui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Person
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.toCurrencyString
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

/**
 * Shows products in a recyclerview. These should be updated automatically when the objects are changed in realm
 */
class ProductRecAdapter(
    val context: Context,
    val realm: Realm,
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

        // make item go up
        holder.upIv.setOnClickListener {
            realm.executeTransaction {
                val other = realm.where(Product::class.java).equalTo("row", product.row - 1).findFirst()
                if (other != null) {
                    other.row += 1
                    product.row -= 1
                }
            }
        }

        // make item go down
        holder.downIv.setOnClickListener {
            realm.executeTransaction {
                val other = realm.where(Product::class.java).equalTo("row", product.row + 1).findFirst()
                if (other != null) {
                    other.row -= 1
                    product.row += 1
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return if (data == null) 0 else data!!.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv = itemView.findViewById<TextView>(R.id.productRecItem_name)!!
        val priceTv = itemView.findViewById<TextView>(R.id.productRecItem_price)!!
        val upIv = itemView.findViewById<ImageView>(R.id.productRecItem_up)!!
        val downIv = itemView.findViewById<ImageView>(R.id.productRecItem_down)!!
    }
}