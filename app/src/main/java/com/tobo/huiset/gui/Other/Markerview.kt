package com.tobo.huiset.gui.Other

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.tobo.huiset.R

/**
 * Constructor. Sets up the MarkerView with a custom layout resource.
 *
 * @param context
 * @param layoutResource the layout resource to use for the MarkerView
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
        topTv.setText(e.x.toInt())
//        bottomTv.setText()
        super.refreshContent(e, highlight)
    }


}


