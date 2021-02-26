package com.example.projetiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.linehistorique_layout.view.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


class HistoriqueActivity :AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var db = FirebaseFirestore.getInstance()
    private var historiques : ArrayList<Historique> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historiques)

        db.collection("oeuvres").document(intent.getStringExtra("idoeuvre")!!).collection("incident")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val year = document.id.split("_")[0].split("-")[2].toInt();
                    val month = document.id.split("_")[0].split("-")[0].toInt();
                    val day = document.id.split("_")[0].split("-")[1].toInt();
                    val hour = document.id.split("_")[1].split(":")[0].toInt();
                    val minute = document.id.split("_")[1].split(":")[1].toInt();
                    val seconde = document.id.split("_")[1].split(":")[2].toInt();

                    val DateIncident: DateTime = DateTime(year, month, day, hour, minute, seconde)
                    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy à HH:mm:ss")
                    var TypeIncident :String = "";
                    if(document.getBoolean("IR") == true)
                        TypeIncident += "Infrarouge"

                    if(document.getBoolean("accelerometer") == true && TypeIncident != "")
                        TypeIncident += " - Accéléromètre"
                    else if (document.getBoolean("accelerometer") == true)
                        TypeIncident += "Accéléromètre"

                    if(document.getBoolean("humidity") == true && TypeIncident != "")
                        TypeIncident += " - Humidité"
                    else if (document.getBoolean("humidity") == true )
                        TypeIncident += "Humidité"

                    if(document.getBoolean("temperature") == true && TypeIncident != "")
                        TypeIncident += " - Température"
                    else if (document.getBoolean("temperature") == true )
                        TypeIncident += "Température"

                    historiques.add(
                        Historique(
                            document.id,
                            DateIncident.toString(fmt),
                            TypeIncident,
                            null
                        )
                    )
                }
                viewManager = LinearLayoutManager(this)
                viewAdapter = HistoriqueAdapter(historiques)
                viewAdapter.notifyDataSetChanged()

                recyclerView = findViewById<RecyclerView>(R.id.hist_list).apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = viewManager

                    // specify an viewAdapter (see also next example)
                    adapter = viewAdapter

                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

    }

    fun logOut(view: View) {
        if(Firebase.auth.currentUser != null){
            Firebase.auth.signOut();
            intent = Intent(this, LoginActivity::class.java);
            startActivity(intent);
        }
    }

    fun historiqueDetails(view: View){
        var idoeuvre = intent.getStringExtra("idoeuvre")!!;
        var titreoeuvre = intent.getStringExtra("titre")!!;
        intent = Intent(this, HistoriqueDetailActivity::class.java)
        for(b in historiques){
            if(b.date == view.item_title1.text.toString()){
                intent.putExtra("idoeuvre", idoeuvre)
                intent.putExtra("titreoeuvre", titreoeuvre)
                intent.putExtra("id", b.id)
                intent.putExtra("date", b.date)
                intent.putExtra("type", b.type)
            }
        }
        startActivity(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val titre = intent.getStringExtra("titre")!!
        val idoeuvre = intent.getStringExtra("idoeuvre")!!
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("titre", titre)
            intent.putExtra("idoeuvre", idoeuvre)
            startActivity(intent)
        }
        return super.onKeyDown(keyCode, event)
    }
}