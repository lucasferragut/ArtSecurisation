package com.example.projetiot

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        button1.setOnClickListener{
            var idoeuvre = intent.getStringExtra("idoeuvre")!!
            var titre = intent.getStringExtra("titre")!!
            intent = Intent(this, AccueilActivity::class.java)
            intent.putExtra("titre",titre)
            intent.putExtra("idoeuvre",idoeuvre)
            startActivity(intent)
        }

        button2.setOnClickListener{
            var idoeuvre = intent.getStringExtra("idoeuvre")!!
            var titre = intent.getStringExtra("titre")!!
            intent = Intent(this, HistoriqueActivity::class.java)
            intent.putExtra("titre",titre)
            intent.putExtra("idoeuvre",idoeuvre)
            startActivity(intent)
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            intent = Intent(this, OeuvresActivity::class.java)
            startActivity(intent)
        }
        return super.onKeyDown(keyCode, event)
    }

}