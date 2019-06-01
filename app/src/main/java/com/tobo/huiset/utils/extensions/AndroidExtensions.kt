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
