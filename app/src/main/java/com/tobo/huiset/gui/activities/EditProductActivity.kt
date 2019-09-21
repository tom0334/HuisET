package com.tobo.huiset.gui.activities

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.extensions.euroToCent
import com.tobo.huiset.utils.extensions.toCurrencyString
import com.tobo.huiset.utils.extensions.toNumberDecimal
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText


/**
 * Edit product
 * accessed by product -> add or product -> edit
 */
class EditProductActivity : HuisEtActivity() {

    private var oldProduct: Product? = null
    private var new: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editproduct)

        priceInitOnlyOneSeperatorValidator()

        // reset old values of product is edited
        val extras = intent.extras
        if (extras != null) {
            val oldId = extras.getString("PRODUCT_ID")
            oldProduct = realm.where(Product::class.java).equalTo("id", oldId).findFirst()!!
            findViewById<EditText>(R.id.name).setText(oldProduct!!.name)
            findViewById<EditText>(R.id.price).setText(oldProduct!!.price.toNumberDecimal())

            val kindRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_kindProd)
            when {
                oldProduct!!.kind == Product.ONLY_TURFABLE -> kindRadioGroup.check(R.id.radio_OnlyTurf_Prod)
                oldProduct!!.kind == Product.ONLY_BUYABLE -> kindRadioGroup.check(R.id.radio_OnlyBuy_Prod)
                else -> kindRadioGroup.check(R.id.radio_Both_Prod)
            }

            val speciesRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_productSpecies)
            when {
                oldProduct!!.species == Product.BEERPRODUCT -> speciesRadioGroup.check(R.id.radio_beerProduct)
                oldProduct!!.species == Product.CRATEPRODUCT -> speciesRadioGroup.check(R.id.radio_crateProduct)
                oldProduct!!.species == Product.SNACKPRODUCT -> speciesRadioGroup.check(R.id.radio_snackProduct)
                else -> speciesRadioGroup.check(R.id.radio_otherProduct)
            }

            new = false
        }
        else {
            showSoftKeyboard(findViewById(R.id.name))
        }
    }

    private fun priceInitOnlyOneSeperatorValidator() {
        val editText = findViewById<TextInputEditText>(R.id.price)
        editText.addTextChangedListener(object : TextWatcher {
            lateinit var sBackup: String

            /**
             * Backup string before comma
             */
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                sBackup = s.toString()
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }

            /**
             * Makes sure only 1 comma or dot is used.
             */
            override fun afterTextChanged(editable: Editable) {
                try {
                    if (editable.toString() != "") {
                        java.lang.Double.valueOf(editable.toString().replace(',', '.'))
                    }
                } catch (e: Exception) {
                    editText.setText(sBackup)
                    editText.setSelection(editText.text.toString().length)
                }

            }
        })
    }

    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editprofile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // product edit/add done
        if (id == R.id.unitDone) {
            doneClicked()
        }
        // product delete
        if (id == R.id.unitDelete) {
            deleteClicked()
        }

        return super.onOptionsItemSelected(item)
    }

    // automatically opens keyboard on startup
    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun deleteClicked() {
        if (new) {
            this.finish()
            return
        }

        // if product isn't new, then ask "are you sure?
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Weet je zeker dat je ${oldProduct!!.name} wil verwijderen?")
            .setPositiveButton("verwijderen") { _, _ ->
                db.removeProduct(oldProduct!!)
                db.updateProductRows()
                this.finish()
            }
            .setNegativeButton("annuleren") { _, _ ->
                // User cancelled the dialog, do nothing
            }
        // Create the AlertDialog object and return it
        builder.create().show()
    }

    private fun doneClicked() {
        val nameEditText = findViewById<EditText>(R.id.name)
        val newName = nameEditText.text.toString()

        val priceEditText = findViewById<EditText>(R.id.price)
        val priceString = priceEditText.text.toString().replace(',','.')

        if (!nameValidate(newName, nameEditText) || !priceValidate(priceString, priceEditText)) {
            return
        }
        val newPrice = priceString.euroToCent()

        val selectedKindButton = findViewById<RadioGroup>(R.id.radiogroup_kindProd).checkedRadioButtonId
        var newKind = Product.BOTH_TURF_AND_BUY
        if (selectedKindButton == R.id.radio_OnlyTurf_Prod) {
            newKind = Product.ONLY_TURFABLE
        }
        else if (selectedKindButton == R.id.radio_OnlyBuy_Prod) {
            newKind = Product.ONLY_BUYABLE
        }

        db.updateProductRows()
        val newRow = db.findAllCurrentProducts(Product.BOTH_TURF_AND_BUY).size

        val selectedSpeciesButton = findViewById<RadioGroup>(R.id.radiogroup_productSpecies).checkedRadioButtonId
        val newSpecies: Int
        newSpecies = when (selectedSpeciesButton) {
            R.id.radio_beerProduct -> Product.BEERPRODUCT
            R.id.radio_crateProduct -> Product.CRATEPRODUCT
            R.id.radio_snackProduct -> Product.SNACKPRODUCT
            else -> Product.OTHERPRODUCT
        }

        realm.executeTransaction {
            if (new) {
                val product = Product.create(newName, newPrice, newKind, newRow, newSpecies)
                realm.copyToRealm(product)
            } else {
                oldProduct!!.name = newName
                oldProduct!!.price = newPrice
                oldProduct!!.kind = newKind
                oldProduct!!.species = newSpecies
            }
        }

        Toast.makeText(
            this,
            "Product $newName of ${newPrice.toCurrencyString()} added/edited",
            Toast.LENGTH_SHORT
        ).show()
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
        val maxPriceLength = 6
        if (price.split('.')[0].length > maxPriceLength) {
            editText.error = "Er mogen niet meer dan $maxPriceLength voor de comma staan"
            return false
        }
        // format should be _.cc
        if (price.contains('.')) {
            if (price.split('.')[1].length > 2) {
                editText.error = "Er mogen maximaal 2 getallen achter de comma"
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
        // duplicate names are not accepted, except if the old product is deleted
        if (realm.where(Product::class.java)
                .equalTo("deleted", false)
                .findAll().map { it.name }
                .count { it.toLowerCase().trim() == name.toLowerCase().trim() } > 0
        ) {
            if (new) {
                editText.error = "Naam bestaat al"
                return false
            }
        }
        // name is too long
        val maxNameLength = 12
        if (name.length > maxNameLength) {
            editText.error = "Naam mag niet langer dan $maxNameLength tekens zijn"
            return false
        }
        return true
    }

    /**
     * hides keyboard when something else is clicked
     * param view is needed.
     */
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}