package com.tobo.huiset.utils.extensions

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView
import com.tobo.huiset.MyApplication
import com.tobo.huiset.utils.ToboTime
import java.sql.Timestamp
import java.util.*

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

fun Long.toTimeAgoString(includeNewLine:Boolean):String{
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
        minutesAgo < 60 -> "$minutesAgo min.\ngeleden"
        hoursAgo < 24 -> "$hoursAgo uur\ngeleden"
        daysAgo < 7 -> "$daysAgo dagen\ngeleden"
        daysAgo < 30 -> "$weeksAgo weken\ngeleden"
        monthsAgo < 12 -> "$monthsAgo  maanden\ngeleden"
        else -> "$yearsAgo jaar\ngeleden"
    }
    return if (includeNewLine) text else text.replace("\n", " ")
}


fun Long.toToboTime(): ToboTime = ToboTime(this)


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