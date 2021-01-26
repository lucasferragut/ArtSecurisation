package com.example.projetiot

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_accueil.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.custom_toolbar.*


class AccueilActivity() : AppCompatActivity() {
    private lateinit var Temperature : ArrayList<Entry>
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)

        oeuvre_title.text = intent.getStringExtra("title")

        // in this example, a LineChart is initialized from xml
        var chart: LineChart = findViewById<LineChart>(R.id.chart)
        Temperature = ArrayList()
        val LineDataSet = LineDataSet(getList(), "Temperature");

        //LineDataSet.setColors(ColorTemplate.createColors(ColorTemplate.MATERIAL_COLORS))
        //Paramètres de notre courbe
        LineDataSet.lineWidth = 1.75f;
        LineDataSet.circleRadius = 2f;
        LineDataSet.circleHoleRadius = 2f;
        LineDataSet.color = Color.RED;
        LineDataSet.setCircleColor(Color.RED);
        LineDataSet.setDrawValues(true);

        //Colore sous la courbe (Filled Line Chart)
        LineDataSet.fillAlpha = 255;
        LineDataSet.setDrawFilled(true);
        LineDataSet.fillColor = Color.rgb(255,146,145);


        //Gère les axes verticaux
        val left: YAxis = chart.axisLeft
        left.setDrawLabels(true) // no axis labels
        left.setDrawAxisLine(true) // no axis line
        left.setDrawGridLines(true) // no grid lines
        left.setDrawZeroLine(true) // draw a zero line
        chart.axisRight.isEnabled = false // no right axis
        left.axisMinimum = 0f; // start at zero
        left.axisMaximum = 40f; // the axis maximum is 100

        //Gère les axes horizontaux
        val xAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f;
        xAxis.axisMaximum = 60f;
        xAxis.setDrawLabels(true)

        chart.description.isEnabled = true;
        chart.description.text = "Temps (60 dernières minutes)";

        chart.data = LineData(LineDataSet);
        chart.animateXY(500, 500);

    }

    fun getList() : ArrayList<Entry>{
        Temperature.add(Entry(0F, 15F))
        Temperature.add(Entry(1F, 20F))
        Temperature.add(Entry(2F, 22F))
        Temperature.add(Entry(4F, 10F))
        Temperature.add(Entry(10F, 1F))
        Temperature.add(Entry(15F, 30F))
        Temperature.add(Entry(15F, 15F))
        return Temperature
    }

}