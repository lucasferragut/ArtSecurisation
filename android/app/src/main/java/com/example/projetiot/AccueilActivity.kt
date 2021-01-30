package com.example.projetiot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_accueil.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList


class AccueilActivity : AppCompatActivity() {
    private lateinit var Temperature : ArrayList<Entry>
    private lateinit var Humidity : ArrayList<Entry>
    private var db = FirebaseFirestore.getInstance()
    private var isAlarmOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)

        oeuvre_title.text = intent.getStringExtra("titre")

        //------------------------------------------------------------- Initialisation et gestion du switch -----------------------------------------------------
        switch1.setOnCheckedChangeListener { _, isChecked ->
            db.collection("oeuvres").document(intent.getStringExtra("titre")!!)
                .update("isAlarmActivated",isChecked)
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error checkbox", Toast.LENGTH_LONG).show()
                }
        }

        db.collection("oeuvres").document(intent.getStringExtra("titre")!!)
            .get()
            .addOnSuccessListener { result -> switch1.isChecked =
                (result.getBoolean("isAlarmActivated") == true)
            }

        //------------------------------------------------------ Initialization des charts Température et humidité ---------------------------------------
        var TemperatureChart: LineChart = findViewById<LineChart>(R.id.TemperatureChart)
        var HumidityChart: LineChart = findViewById<LineChart>(R.id.HumidityChart)
        Temperature = ArrayList()
        Humidity = ArrayList()
        val LineDataSetTemperature = LineDataSet(getTemperatureList(), "Temperature");
        val LineDataSetHumidity = LineDataSet(getHumidityList(), "Humidité");

        //----------------------------------------------------------------- Graphique Température ---------------------------------------------------------
        //Paramètres de notre courbe
        LineDataSetTemperature.lineWidth = 1.75f;
        LineDataSetTemperature.circleRadius = 2f;
        LineDataSetTemperature.circleHoleRadius = 2f;
        LineDataSetTemperature.color = Color.RED;
        LineDataSetTemperature.setCircleColor(Color.RED);
        LineDataSetTemperature.setDrawValues(true);

        //Colore sous la courbe (Filled Line Chart)
        LineDataSetTemperature.fillAlpha = 255;
        LineDataSetTemperature.setDrawFilled(true);
        LineDataSetTemperature.fillColor = Color.rgb(255, 146, 145);


        //Gère les axes verticaux
        val left: YAxis = TemperatureChart.axisLeft
        left.setDrawLabels(true) // no axis labels
        left.setDrawAxisLine(true) // no axis line
        left.setDrawGridLines(true) // no grid lines
        left.setDrawZeroLine(true) // draw a zero line
        TemperatureChart.axisRight.isEnabled = false // no right axis
        left.axisMinimum = 0f; // start at zero
        left.axisMaximum = 40f; // the axis maximum is 100

        //Gère les axes horizontaux
        val xAxis = TemperatureChart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f;
        xAxis.axisMaximum = 60f;
        xAxis.setDrawLabels(true)

        TemperatureChart.description.isEnabled = true;
        TemperatureChart.description.text = "Temps (60 dernières minutes)";

        TemperatureChart.data = LineData(LineDataSetTemperature);
        TemperatureChart.animateXY(500, 500);

        //----------------------------------------------------------- Graphique humidité ----------------------------------------------------------
        //Paramètres de notre courbe
        LineDataSetHumidity.lineWidth = 1.75f;
        LineDataSetHumidity.circleRadius = 2f;
        LineDataSetHumidity.circleHoleRadius = 2f;
        LineDataSetHumidity.color = Color.BLUE;
        LineDataSetHumidity.setCircleColor(Color.BLUE);
        LineDataSetHumidity.setDrawValues(true);

        //Colore sous la courbe (Filled Line Chart)
        LineDataSetHumidity.fillAlpha = 255;
        LineDataSetHumidity.setDrawFilled(true);
        LineDataSetHumidity.fillColor = Color.rgb(51, 204, 255);


        //Gère les axes verticaux
        val left2: YAxis = HumidityChart.axisLeft
        left2.setDrawLabels(true) // no axis labels
        left2.setDrawAxisLine(true) // no axis line
        left2.setDrawGridLines(true) // no grid lines
        left2.setDrawZeroLine(true) // draw a zero line
        HumidityChart.axisRight.isEnabled = false // no right axis
        left2.axisMinimum = 0f; // start at zero
        left2.axisMaximum = 40f; // the axis maximum is 100

        //Gère les axes horizontaux
        val xAxis2 = HumidityChart.xAxis
        xAxis2.position = XAxisPosition.BOTTOM
        xAxis2.textSize = 10f
        xAxis2.setDrawAxisLine(true)
        xAxis2.setDrawGridLines(false)
        xAxis2.axisMinimum = 0f;
        xAxis2.axisMaximum = 60f;
        xAxis2.setDrawLabels(true)

        HumidityChart.description.isEnabled = true;
        HumidityChart.description.text = "Temps (60 dernières minutes)";

        HumidityChart.data = LineData(LineDataSetHumidity);
        HumidityChart.animateXY(500, 500);

    }

    private fun getTemperatureList() : ArrayList<Entry>{
        val lastHour: DateTime = DateTime().minusHours(1)
        db.collection("oeuvres").document(intent.getStringExtra("titre")!!).collection("humidityAndTemperature")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val currentTime = DateTime(document.id.split("_")[0].split("-")[2].toInt(), document.id.split("_")[0].split("-")[0].toInt(), document.id.split("_")[0].split("-")[1].toInt(), document.id.split("_")[1].split(":")[0].toInt(), document.id.split("_")[1].split(":")[1].toInt(), document.id.split("_")[1].split(":")[2].toInt());
                    if(currentTime.isAfter(lastHour)){
                        Temperature.add(
                            Entry(
                                currentTime.minuteOfHour().toString().toFloat(),
                                document.getDouble("temperature")!!.toFloat()
                            )
                        );
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        return Temperature
    }

    private fun getHumidityList() : ArrayList<Entry>{
        val lastHour: DateTime = DateTime().minusHours(1)
        db.collection("oeuvres").document(intent.getStringExtra("titre")!!).collection("humidityAndTemperature")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val year = document.id.split("_")[0].split("-")[2].toInt();
                    val month = document.id.split("_")[0].split("-")[0].toInt();
                    val day = document.id.split("_")[0].split("-")[1].toInt();
                    val hour = document.id.split("_")[1].split(":")[0].toInt();
                    val minute = document.id.split("_")[1].split(":")[1].toInt();
                    val seconde = document.id.split("_")[1].split(":")[2].toInt();
                    val currentTime = DateTime(year,month , day , hour, minute , seconde);
                    if(currentTime.isAfter(lastHour)){
                        Humidity.add(
                            Entry(
                                currentTime.minuteOfHour().toString().toFloat(),
                                document.getDouble("humidity")!!.toFloat()
                            )
                        );
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
        return Humidity
    }

    fun logOut(view: View) {
        if(Firebase.auth.currentUser != null){
            Firebase.auth.signOut();
            intent = Intent(this, LoginActivity::class.java);
            startActivity(intent);
        }
    }

    fun switchAlarmState(view: View) {

        //On récupère l'état actuel de l'alarme
        db.collection("oeuvres").document(intent.getStringExtra("titre")!!)
            .get()
            .addOnSuccessListener {
                result -> isAlarmOn = (result.getBoolean("isAlarmOn") == true)
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

        //On mdoifie l'état de l'alarme
        db.collection("oeuvres").document(intent.getStringExtra("titre")!!)
            .update("isAlarmOn", !isAlarmOn)
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error updating documents: ", exception)
            }
    }

}