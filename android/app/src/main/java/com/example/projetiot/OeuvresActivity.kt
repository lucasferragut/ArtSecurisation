package com.example.projetiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.line_layout.view.*



class OeuvresActivity: AppCompatActivity()  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var db = FirebaseFirestore.getInstance()
    private var oeuvres : ArrayList<Oeuvre> = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oeuvres)

        //setSupportActionBar(myToolbar)

        db.collection("oeuvres")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    oeuvres.add(
                        Oeuvre(
                            document.id!!,
                            document.getString("artist")!!,
                            document.getString("artMovement")!!,
                            document.getDouble("date")!!,
                            document.getString("technique")!!
                        )
                    )
                }
                viewManager = LinearLayoutManager(this)
                viewAdapter = OeuvreAdapter(oeuvres)
                viewAdapter.notifyDataSetChanged()

                recyclerView = findViewById<RecyclerView>(R.id.oeuvres_list).apply {
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

    fun oeuvreDetails(view: View){
        intent = Intent(this, MenuActivity::class.java)
        for(b in oeuvres){
            if(b.titre == view.item_title.text.toString()){
                intent.putExtra("titre", b.titre)
            }
        }
        startActivity(intent)
    }


    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    fun logOut(view: View) {
        if(Firebase.auth.currentUser != null){
            Firebase.auth.signOut();
            intent = Intent(this, LoginActivity::class.java);
            startActivity(intent);
        }
    }

}