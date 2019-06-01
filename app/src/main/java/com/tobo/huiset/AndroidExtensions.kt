package com.tobo.huiset

import android.content.Context
import android.util.TypedValue

fun Int.toPixel(context: Context) :Int{
    val r = context.resources
    val pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this.toFloat(), r.getDisplayMetrics())
    return  pixels.toInt()
}