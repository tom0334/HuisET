package com.tobo.huiset.gui.activities

import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.realmModels.Person
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.SimpleDateFormat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tobo.huiset.gui.Other.CustomMarkerView
import com.tobo.huiset.utils.DoubleLineXaxisRenderer
import java.util.*

class StatsActivity : HuisEtActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val chart= findViewById<LineChart>(R.id.chart)

        val persons = db.findAllCurrentPersons()
        val datasets = persons.map { createDataForPerson(it) }
        
        val lineData = LineData(datasets)
        chart.data = lineData
        styleChart(chart)
    }

    fun styleChart(chart: LineChart) {
        chart.marker = CustomMarkerView(this,R.layout.marker_view)
        chart.setDrawMarkers(true)

        chart.description = null

        chart.legend.setDrawInside(true)
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        chart.legend.yOffset = 20f
        chart.legend.xOffset = 20f

        //Set up the amount of spacing between lines

        chart.xAxis.spaceMin = chart.xAxis.mAxisRange * 0.15f
        chart.xAxis.spaceMax = chart.xAxis.mAxisRange * 0.15f

        chart.axisLeft.isGranularityEnabled = true
        chart.axisLeft.granularity = 1f

        chart.axisRight.isGranularityEnabled = true
        chart.axisRight.granularity = 1f

        chart.axisLeft.mAxisMinimum = 0.0f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM


        chart.xAxis.labelRotationAngle = -30f
        chart.xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat = SimpleDateFormat("HH:mm\ndd MMM")

            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(value.toLong()))
            }
        }

        chart.setXAxisRenderer(
            DoubleLineXaxisRenderer(
                chart.getViewPortHandler(),
                chart.getXAxis(),
                chart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )


        chart.invalidate() // refresh
    }

    fun createDataForPerson(p: Person):ILineDataSet {
        val entries = getEntriesForPerson(p)
        val dSet = LineDataSet(entries, p.name)
        dSet.setCircleColor(Color.parseColor(p.color))
        dSet.setDrawCircleHole(true)
        dSet.setDrawValues(false)

        dSet.color = Color.parseColor(p.color)
        return dSet

    }


    fun getEntriesForPerson(p:Person):List<Entry>{
        val transactions = db.getTransactions(buy = false,personId = p.id)

        var soFar = 0
        //todo add 0 at profile createon date if needed


        val entries:MutableList<Entry> = mutableListOf()
        for(t in transactions){
            soFar += t.amount

            val x = t.time
            val y = soFar.toFloat()

            entries.add(Entry(x.toFloat(),y))
        }
        return entries.sortedBy { it.x }

    }
}


