package com.tobo.huiset.utils.extensions

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView

fun Int.toPixel(context: Context) :Int{
    val r = context.resources
    val pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this.toFloat(), r.getDisplayMetrics())
    return  pixels.toInt()
}

fun TextView.setTextColorFromHex(hex:String) = this.setTextColor(Color.parseColor(hex))

fun Int.toCurrencyString(): String {
    val signed = if (this < 0) "-" else ""
    val euros = Integer.toString(this / 100)
    val abscents = Math.abs(this % 100)
    var cents = if (abscents < 10) "0" else ""
    cents += Integer.toString(abscents)
    return "€$signed$euros,$cents"
}