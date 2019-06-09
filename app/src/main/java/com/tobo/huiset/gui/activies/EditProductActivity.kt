package com.tobo.huiset.gui.activies

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.EditText
import android.widget.RadioGroup
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.R
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.euroToCent
import com.tobo.huiset.utils.extensions.toCurrencyString
import com.tobo.huiset.utils.extensions.toNumberDecimal


/**
 * Edit product
 * accessed by product -> add or product -> edit
 */
class EditProductActivity : HuisEtActivity() {

    private var oldProduct : Product? = null
    private var new : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editproduct)

        val extras = intent.extras
        if (extras != null) {
            val oldId = extras.getString("PRODUCT_ID")
            oldProduct = realm.where(Product::class.java).equalTo("id", oldId).findFirst()!!
            findViewById<EditText>(R.id.name).setText(oldProduct!!.name)
            findViewById<EditText>(R.id.price).setText(oldProduct!!.price.toNumberDecimal())
            new = false
        }
    }

    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_editprofile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        // product edit/add done
        if (id == R.id.profiledone) {
            doneClicked()
        }
        if (id == R.id.profiledelete) {
            deleteClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteClicked() {
        // if product isn't new, then ask "are you sure?
        if (!new) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Weet je zeker dat je ${oldProduct!!.name} wil verwijderen?")
                .setPositiveButton("verwijderen") { dialog, id ->
                    // Delete the product from the realm
                    realm.executeTransaction {
                        oldProduct!!.deleteFromRealm()
                    }
                    this.finish()
                }
                .setNegativeButton("annuleren") { dialog, id ->
                    // User cancelled the dialog, do nothing
                }
            // Create the AlertDialog object and return it
            builder.create().show()
        }
        else {
            this.finish()
        }
    }

    private fun doneClicked() {
        val nameEditText = findViewById<EditText>(R.id.name)
        val newName = nameEditText.text.toString()

        val priceEditText = findViewById<EditText>(R.id.price)
        val priceString = priceEditText.text.toString()

        if (!nameValidate(newName, nameEditText) || !priceValidate(priceString, priceEditText)) {
            return
        }
        val newPrice = priceString.euroToCent()

        val radioShowGroup = findViewById<RadioGroup>(R.id.radiogroup_showprod).checkedRadioButtonId
        var showBool = false
        if (radioShowGroup == R.id.radioShowProd) {
            showBool = true
        }

        realm.executeTransaction {
            if (new) {
                val product = Product.create(newName, newPrice, showBool)
                realm.copyToRealm(product)
            }
            else {
                oldProduct!!.name = newName
                oldProduct!!.price = newPrice
                oldProduct!!.show = showBool
            }
        }

        Toast.makeText(this, "Product $newName of ${newPrice.toCurrencyString()} added/edited, shown $showBool", Toast.LENGTH_SHORT).show()
        this.finish()
    }

    /**
     * Validates the input price
     */
    private fun priceValidate(price: String, editText: EditText): Boolean {
        // empty fields are not accepted
        if (price == "") {
            editText.error = "Vul een prijs in"
            return false
        }
        // name is too long
        val maxPriceLength = 8
        if (price.split('.')[0].length > maxPriceLength) {
            editText.error = "Er mogen niet meer dan $maxPriceLength voor de comma staan"
            return false
        }
        // format should be _.cc
        if (price.contains('.')) {
            if (price.split('.')[1].length != 2) {
                editText.error = "Er moeten 2 getallen achter de comma"
                return false
            }
        }

        return true
    }

    /**
     * Validates the input name
     */
    private fun nameValidate(name: String, editText: EditText): Boolean {
        // empty fields are not accepted
        if (name == "") {
            editText.error = "Vul een naam in"
            return false
        }
        // duplicate names are not accepted
        if (realm.where(Product::class.java).equalTo("name", name).count() > 0) {
            if (new) {
                editText.error = "Naam bestaat al"
                return false
            }
        }
        // name is too long
        val maxNameLength = 20
        if (name.length > maxNameLength) {
            editText.error = "Naam mag niet langer dan $maxNameLength tekens zijn"
            return false
        }
        return true
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}