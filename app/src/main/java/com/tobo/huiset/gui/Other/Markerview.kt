package com.tobo.huiset.gui.Other

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.tobo.huiset.R
import java.text.SimpleDateFormat

/**
 * This is he view that is shown when clicking on a datapoint in a graph
 */
class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val bottomTv: TextView = findViewById(R.id.markerViewDate)
    private val topTv: TextView = findViewById(R.id.markerViewTV)
    private var mOffset: MPPointF? = null

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return mOffset!!
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        topTv.setText("${e.y.toInt()} bier")
        val date = e.x.toLong()
        val mFormat = SimpleDateFormat("dd-MM HH:mm")
        bottomTv.text = mFormat.format(date)
        super.refreshContent(e, highlight)
    }


}
