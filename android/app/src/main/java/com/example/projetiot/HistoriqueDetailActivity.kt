package com.example.projetiot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_historiquedetails.*
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class HistoriqueDetailActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var historiquesDetails: ArrayList<Historique> = ArrayList();
    private var db = FirebaseFirestore.getInstance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historiquedetails)

        title_anomalie.text = "Anomalie du " + intent.getStringExtra("date")!!

        db.collection("oeuvres").document(intent.getStringExtra("idoeuvre")!!)
            .collection("incident").document(intent.getStringExtra("id")!!).collection("default")
            .get()
            .addOnSuccessListener { result ->

                var DateStartIncident: DateTime = DateTime()
                var i: Int = 0;
                for (document in result) {
                    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss")
                    var sensor_name = ""
                    when(document.getString("sensor")){
                        "accelerometer" -> sensor_name = "Accéléromètre"
                        "IR" -> sensor_name = "Infra-rouge"
                        "temperature" -> sensor_name = "Température"
                        "humidity" -> sensor_name = "Humidité"
                    }

                    val year = document.getString("dateTime")!!.split("_")[0].split("-")[2].toInt();
                    val month = document.getString("dateTime")!!.split("_")[0].split("-")[0].toInt();
                    val day = document.getString("dateTime")!!.split("_")[0].split("-")[1].toInt();
                    val hour = document.getString("dateTime")!!.split("_")[1].split(":")[0].toInt();
                    val minute = document.getString("dateTime")!!.split("_")[1].split(":")[1].toInt();
                    val seconde = document.getString("dateTime")!!.split("_")[1].split(":")[2].toInt();
                    val DateDocument = DateTime(year, month, day, hour, minute, seconde)

                    if(i == 0){
                        DateStartIncident = DateDocument
                        name_sensor.text = sensor_name
                        date_sensor.text = DateDocument.toString(fmt)
                        infos_sensor.text = document.getString("value")
                    }


                    if (i == result.size() - 1) {
                        duration_anomalie.text = "Durée de l'anomalie : " + Interval(
                            DateStartIncident,
                            DateDocument
                        ).toDuration().standardSeconds + " secondes"
                    }

                    if(i != 0)
                        historiquesDetails.add(
                            Historique(
                                document.id,
                                DateDocument.toString(fmt),
                                sensor_name,
                                document.getString("value")
                            )
                        )
                    i += 1
                }

                viewManager = LinearLayoutManager(this)
                viewAdapter = HistoriqueDetailAdapter(historiquesDetails)
                viewAdapter.notifyDataSetChanged()

                recyclerView = findViewById<RecyclerView>(R.id.historiquedetails_list).apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = viewManager

                    // specify an viewAdapter (see also next example)
                    adapter = viewAdapter
                }
            }
    }

    fun logOut(view: View) {
        if(Firebase.auth.currentUser != null){
            Firebase.auth.signOut();
            intent = Intent(this, LoginActivity::class.java);
            startActivity(intent);
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val titre = intent.getStringExtra("titreoeuvre")!!
        val idoeuvre = intent.getStringExtra("idoeuvre")!!
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent = Intent(this, HistoriqueActivity::class.java)
            intent.putExtra("titre", titre)
            intent.putExtra("idoeuvre", idoeuvre)
            startActivity(intent)
        }
        return super.onKeyDown(keyCode, event)
    }
}