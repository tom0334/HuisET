package com.tobo.huiset.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

object HandyFunctions {

    /**
     * Validates the input name
     */
    fun nameValidate(
        name: String,
        editText: EditText,
        db: HuisETDB,
        checkForDuplicateName: Boolean,
        zeroIfPerson_oneIfProduct: Int,
        isHuisRekening: Boolean = false
    ): Boolean {

        // empty fields are not accepted
        if (name == "") {
            editText.error = "Vul een naam in"
            return false
        }

        // name is huisrekening
        if (zeroIfPerson_oneIfProduct == 0) { // if person
            if (name.toLowerCase().trim() == "huisrekening" && !isHuisRekening) {
                editText.error = "Huisrekening kan alleen via instellingen aangezet worden"
                return false
            }
        }

        if (zeroIfPerson_oneIfProduct == 1) { // if product
            if (name.trim() == "Balans") {
                editText.error = "Product mag niet Balans heten"
                return false
            }

            if (name.trim() == "Ontvangen") {
                editText.error = "Product mag niet Ontvangen heten"
                return false
            }

            if (name.trim() == "Overgemaakt") {
                editText.error = "Product mag niet Overgemaakt heten"
                return false
            }
        }

        // duplicate names are not accepted, except if the old person is deleted
        if (checkForDuplicateName
            && db.findDuplicatePersonName(name, zeroIfPerson_oneIfProduct)
        ) {
            editText.error = "Naam bestaat al"
            return false
        }

        // name is too long
        val maxNameLength = 20
        if (name.length > maxNameLength) {
            editText.error = "Naam mag niet langer dan $maxNameLength tekens zijn"
            return false
        }

        return true
    }

    /**
     * Validates the input price
     */
    fun priceValidate(price: String, editText: EditText): Boolean {
        // empty fields are not accepted
        if (price == "") {
            editText.error = "Vul een prijs in"
            return false
        }
        // name is too long
        val maxPriceLength = 6
        if (price.split('.')[0].length > maxPriceLength) {
            editText.error = "Er mogen niet meer dan $maxPriceLength voor de komma staan"
            return false
        }

        return true
    }

    //this makes it only possible to input valid prices, only 2 numbers after a "." or a "," are allowed
    fun addPriceTextLimiter(editText: EditText) {
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
             * And only 2 decimals are allowed.
             */
            override fun afterTextChanged(editable: Editable) {
                try {
                    val s = editable.toString()

                    // Make sure only 1 comma or dot is used
                    if (s != "") {
                        java.lang.Double.valueOf(editable.toString().replace(',', '.'))
                    }

                    // format should be _.cc (only 2 decimals)
                    if (s.contains(',') && editable.toString().split(",")[1].length > 2) {
                        editText.setText(sBackup)
                        editText.setSelection(editText.text.toString().length)
                        editText.error = "Er mogen maximaal 2 getallen achter de komma staan"
                    }
                    if (s.contains('.') && editable.toString().split(".")[1].length > 2) {
                        editText.setText(sBackup)
                        editText.setSelection(editText.text.toString().length)
                        editText.error = "Er mogen maximaal 2 getallen achter de komma staan"
                    }
                } catch (e: Exception) {
                    editText.setText(sBackup)
                    editText.setSelection(editText.text.toString().length)
                }
            }
        })
    }

    /**
     * Validates buyPerAmount amount
     */
    fun buyPerAmountValidate(amount: String, editText: EditText): Boolean {
        if (amount == "") {
            editText.error = "Vul een aantal in"
            return false
        }
        // amount is 0 or negative
        if (amount.toInt() < 1) {
            editText.error = "Aantal moet minimaal 1 zijn"
            return false
        }
        // too big amount
        val maxPriceLength = 2
        if (amount.toString().length > maxPriceLength) {
            editText.error = "Aantal mag niet meer dan $maxPriceLength tekens lang zijn"
            return false
        }

        return true
    }
}