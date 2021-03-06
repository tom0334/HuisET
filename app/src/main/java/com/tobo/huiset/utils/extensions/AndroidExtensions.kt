package com.tobo.huiset.utils.extensions

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tobo.huiset.utils.ToboTime
import java.text.SimpleDateFormat

fun Int.toPixel(context: Context): Int {
    val r = context.resources
    val pixels =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), r.displayMetrics)
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

fun Long.toMixedTimeString(): String {
    val secondsAgo = (System.currentTimeMillis() - this) / 1000
    val minutesAgo = secondsAgo / 60
    val hoursAgo = minutesAgo / 60
    val daysAgo = hoursAgo / 24

    if (daysAgo <= 7) {
        return this.toTimeAgoString(false)
    } else {
        val format = SimpleDateFormat("EEEE d MMM yyyy")
        return format.format(this)
    }
}

fun Long.toTimeAgoString(includeNewLine: Boolean): String {
    val secondsAgo = (System.currentTimeMillis() - this) / 1000
    val minutesAgo = secondsAgo / 60
    val hoursAgo = minutesAgo / 60
    val daysAgo = hoursAgo / 24
    val weeksAgo = daysAgo / 7

    //#yolo
    val monthsAgo = daysAgo / 30
    val yearsAgo = monthsAgo / 12


    val text = when {
        secondsAgo < 10 -> "Nu"
        secondsAgo < 60 -> "Zojuist"
        minutesAgo < 60 -> "$minutesAgo min."
        hoursAgo < 24 -> "$hoursAgo uur"
        daysAgo < 7 -> "$daysAgo dagen"
        daysAgo < 30 -> "$weeksAgo weken"
        monthsAgo < 12 -> "$monthsAgo  maanden"
        else -> "$yearsAgo jaar"
    }
    return if (includeNewLine) text else text.replace("\n", " ")
}


fun Long.toToboTime(): ToboTime = ToboTime(this)

/**
 * green if positiv
 * black if equal aan 0
 * red if negativ
 */
fun Int.getBalanceColorString(): String {
    return when {
        this > 0 -> "#388e3c"
        this == 0 -> "#000000"
        else -> "#dd2c00"
    }
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

fun AppCompatActivity.getDisplayWith(): Float {
    return resources.displayMetrics.widthPixels.toFloat()
}

fun AppCompatActivity.getDisplayHeight(): Float {
    return resources.displayMetrics.heightPixels.toFloat()
}


//sumbyFloat does not exist.
/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 */
inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Float = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}


fun Float.toFormattedAmount(): String {
    var formatted = "%.2f".format(this)
    //remove the last one, that is the  zero in "123.50". Then
    while (formatted.last() === '0') {
        formatted = formatted.dropLast(1)
    }

    //remove the trailing . or , if needed
    if (!formatted.last().isDigit()) {
        formatted = formatted.dropLast(1)
    }

    return formatted
}

