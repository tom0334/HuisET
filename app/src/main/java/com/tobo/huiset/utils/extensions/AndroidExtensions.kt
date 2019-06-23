package com.tobo.huiset.utils.extensions

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView

fun Int.toPixel(context: Context): Int {
    val r = context.resources
    val pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), r.displayMetrics)
    return pixels.toInt()
}

fun TextView.setTextColorFromHex(hex: String) = this.setTextColor(Color.parseColor(hex))

fun Int.toCurrencyString(): String {
    val signed = if (this < 0) "-" else ""
    val euros = Integer.toString(Math.abs(this / 100))
    val abscents = Math.abs(this % 100)
    var cents = if (abscents < 10) "0" else ""
    cents += Integer.toString(abscents)
    return "€$signed$euros,$cents"
}

fun String.euroToCent(): Int {
    var result = 0

    if (this.contains('.')) {
        val split = this.split('.')
        result += Integer.parseInt(split[0]) * 100
        if (split[1].length == 1) {
            result += Integer.parseInt(split[1]) * 10
        }
        else if (split[1].length == 2) {
            result += Integer.parseInt(split[1])
        }
    } else {
        result += Integer.parseInt(this) * 100
    }
    return result
}

fun Int.toNumberDecimal(): String {
    val signed = if (this < 0) "-" else ""
    val euros = Integer.toString(this / 100)
    val abscents = Math.abs(this % 100)
    var cents = if (abscents < 10) "0" else ""
    cents += Integer.toString(abscents)
    return "$signed$euros.$cents"
}

/**
 * Helper function that gets the editor and calls the sharedpreferences apply and
 */
inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}