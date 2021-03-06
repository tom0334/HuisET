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
import com.google.android.material.textfield.TextInputEditText
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.realmModels.Product
import com.tobo.huiset.utils.HandyFunctions
import com.tobo.huiset.utils.extensions.toCurrencyString
import com.tobo.huiset.utils.extensions.toNumberDecimal


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

        val priceEditText = findViewById<TextInputEditText>(R.id.price)
        //this makes it only possible to input valid prices, only 2 numbers after a "." or a "," are allowed
        HandyFunctions.addPriceTextLimiter(priceEditText)

        // reset old values of product is edited
        val extras = intent.extras
        if (extras != null) {
            val oldId = extras.getString("PRODUCT_ID")
            oldProduct = realm.where(Product::class.java).equalTo("id", oldId).findFirst()!!

            val nameEditText = findViewById<EditText>(R.id.name)
            nameEditText.setText(oldProduct!!.name)
            nameEditText.requestFocus()

            findViewById<EditText>(R.id.price).setText(oldProduct!!.price.toNumberDecimal())

            val kindRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_kindProd)
            when (oldProduct!!.kind) {
                Product.KIND_TURFABLE -> kindRadioGroup.check(R.id.radio_OnlyTurf_Prod)
                Product.KIND_BUYABLE -> kindRadioGroup.check(R.id.radio_OnlyBuy_Prod)
                Product.KIND_BOTH -> kindRadioGroup.check(R.id.radio_Both_Prod)
                else -> kindRadioGroup.check(R.id.radio_Neither_Prod)
            }

            val speciesRadioGroup = findViewById<RadioGroup>(R.id.radiogroup_productSpecies)
            when (oldProduct!!.species) {
                Product.SPECIES_BEER -> speciesRadioGroup.check(R.id.radio_beerProduct)
                Product.SPECIES_SNACK -> speciesRadioGroup.check(R.id.radio_snackProduct)
                else -> speciesRadioGroup.check(R.id.radio_otherProduct)
            }

            findViewById<EditText>(R.id.buyPerAmount).setText(oldProduct!!.buyPerAmount.toString())

            new = false
        } else {
            showSoftKeyboard(findViewById(R.id.name))
        }
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
                Toast.makeText(
                    this,
                    "Product ${oldProduct!!.name} is verwijderd",
                    Toast.LENGTH_SHORT
                ).show()
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
        val priceString = priceEditText.text.toString()

        val amountEditText = findViewById<EditText>(R.id.buyPerAmount)
        val amountString = amountEditText.text.toString()

        if (!HandyFunctions.nameValidate(newName, nameEditText, db, new, 1)
            || !HandyFunctions.priceValidate(priceString, priceEditText)
            || !HandyFunctions.buyPerAmountValidate(amountString, amountEditText)
        ) {
            return
        }

        val newPrice = HandyFunctions.euroToCent(priceString)
        val newAmount = amountString.toInt()

        val selectedKindButton =
            findViewById<RadioGroup>(R.id.radiogroup_kindProd).checkedRadioButtonId
        val newKind = when (selectedKindButton) {
            R.id.radio_OnlyTurf_Prod -> Product.KIND_TURFABLE
            R.id.radio_OnlyBuy_Prod -> Product.KIND_BUYABLE
            R.id.radio_Both_Prod -> Product.KIND_BOTH
            else -> Product.KIND_NEITHER
        }

        db.updateProductRows()
        val newRow = db.findAllCurrentProducts(Product.KIND_BOTH).size

        val selectedSpeciesButton =
            findViewById<RadioGroup>(R.id.radiogroup_productSpecies).checkedRadioButtonId
        val newSpecies = when (selectedSpeciesButton) {
            R.id.radio_beerProduct -> Product.SPECIES_BEER
            R.id.radio_snackProduct -> Product.SPECIES_SNACK
            else -> Product.SPECIES_OTHER
        }

        if (new) {
            db.createProduct(newName, newPrice, newKind, newRow, newSpecies, newAmount)
        } else {
            db.editProduct(oldProduct!!, newName, newPrice, newKind, newSpecies, newAmount)
        }

        Toast.makeText(
            this,
            "Product $newName van ${newPrice.toCurrencyString()} per $newAmount stuk(s) toegevoegd/aangepast",
            Toast.LENGTH_SHORT
        ).show()
        this.finish()
    }

    /**
     * hides keyboard when something else is clicked
     * param view is needed.
     */
    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}