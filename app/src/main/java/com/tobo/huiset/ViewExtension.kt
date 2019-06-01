package com.tobo.huiset

import android.graphics.Color
import android.widget.TextView

fun TextView.setTextColorFromHex(hex:String) = this.setTextColor(Color.parseColor(hex))
