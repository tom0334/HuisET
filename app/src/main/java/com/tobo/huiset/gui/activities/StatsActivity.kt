package com.tobo.huiset.gui.activities

import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.tobo.huiset.R
import com.tobo.huiset.extendables.HuisEtActivity
import com.tobo.huiset.realmModels.Person
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.tobo.huiset.achievements.amountOfProducts
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.components.YAxis
import com.tobo.huiset.utils.CustomXAxisRenderer





class StatsActivity : HuisEtActivity() {
    lateinit var chart:LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        chart= findViewById<LineChart>(R.id.chart)

        val persons = db.findAllCurrentPersons()
        val datasets = persons.map { createDataForPerson(it) }

        chart.setXAxisRenderer(
            CustomXAxisRenderer(
                chart.getViewPortHandler(),
                chart.getXAxis(),
                chart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        val lineData = LineData(datasets)
        chart.data = lineData


        chart.xAxis.spaceMax = 43200000f
        chart.xAxis.spaceMin = 43200000f

        chart.xAxis.labelRotationAngle = -30f

        chart.axisLeft.isGranularityEnabled = true
        chart.axisLeft.granularity = 1f

        chart.axisRight.isEnabled = false




        chart.axisLeft.mAxisMinimum = 0.0f


        chart.xAxis.valueFormatter = object : ValueFormatter() {

            private val mFormat = SimpleDateFormat("HH:mm\ndd MMM")

            override fun getFormattedValue(value: Float): String {
                return mFormat.format(Date(value.toLong()))
            }
        }


        chart.invalidate() // refresh
    }

    fun createDataForPerson(p: Person):ILineDataSet {
        val entries = getEntriesForPerson(p)
        val dSet = LineDataSet(entries, p.name)

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


