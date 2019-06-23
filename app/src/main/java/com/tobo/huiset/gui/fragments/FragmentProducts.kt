import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtFragment
import com.tobo.huiset.gui.activities.EditProductActivity
import com.tobo.huiset.gui.adapters.ProductRecAdapter
import com.tobo.huiset.utils.ItemClickSupport
import com.tobo.huiset.utils.extensions.findAllCurrentProducts

class FragmentProducts : HuisEtFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()

        // delete the ItemClickSupport
        val rec = view?.findViewById<RecyclerView>(R.id.productsTabRec)
        if (rec != null) {
            ItemClickSupport.removeFrom(rec)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val products = realm.findAllCurrentProducts()

        // this sets up the recyclerview to show the products
        val rec = view.findViewById<RecyclerView>(R.id.productsTabRec)
        rec.addItemDecoration(DividerItemDecoration(rec.context, DividerItemDecoration.VERTICAL))
        rec.adapter = ProductRecAdapter(this.context!!, realm, products, true)
        rec.layoutManager = LinearLayoutManager(this.context)


        val fab = view.findViewById<FloatingActionButton>(R.id.add_product)

        // hides the fab add_profile when scrolling down
        rec.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 2)
                    fab.hide()
                else if (dy <= 2)
                    fab.show()
            }
        })

        // opens EditProfileActivity when fab add_profile is clicked
        fab.setOnClickListener {
            val intent = Intent(this.activity, EditProductActivity::class.java)
            startActivity(intent)
        }

        // opens EditProductActivity on the correct product if a product is clicked
        ItemClickSupport.addTo(rec).setOnItemClickListener { _, position, _ ->
            val product = products!![position]
            val intent = Intent(this.activity, EditProductActivity::class.java)
                .putExtra("PRODUCT_ID", product?.id)
            startActivity(intent)
        }
    }

}