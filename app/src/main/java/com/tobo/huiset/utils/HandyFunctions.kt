package com.tobo.huiset.utils

import android.widget.EditText

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
        if (zeroIfPerson_oneIfProduct == 0
            && name.toLowerCase().trim() == "huisrekening"
            && !isHuisRekening
        ) {
            editText.error = "Huisrekening kan alleen via instellingen aangezet worden"
            return false
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