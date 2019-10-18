package com.tobo.huiset.utils

import android.widget.EditText

object HandyFunctions {

    /**
     * Validates the input name
     */
    fun nameValidate(name: String, editText: EditText, db: HuisETDB): Boolean {

        // empty fields are not accepted
        if (name == "") {
            editText.error = "Vul een naam in"
            return false
        }

        // duplicate names are not accepted, except if the old person is deleted
        if (db.findDuplicatePersonName(name)) {
//            if (new) {
            editText.error = "Naam bestaat al"
            return false
//            }
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
}