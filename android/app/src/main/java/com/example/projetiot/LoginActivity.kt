package com.example.projetiot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
            Toast.makeText(baseContext, "Vous etes connecté en tant que"+currentUser.email.toString(),
                Toast.LENGTH_SHORT).show()
        }
    }

    fun SignIn(email: String, password: String){
        if(email.isEmpty()){
            Toast.makeText(baseContext, "Vous devez renseigner l'email",
                Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()){
            Toast.makeText(baseContext, "Vous devez renseigner le mot de passe",
                Toast.LENGTH_SHORT).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success")
                        Toast.makeText(baseContext, "Authentication success.",
                            Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        //intent = Intent(this, ListActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    fun clickConnexion(view: View) {
        SignIn(etmail.text.toString(), etmdp.text.toString())
    }

    fun createAccount(view: View) {
        var email = etmail.text.toString()
        var password = etmdp.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Sign up success.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign up failed.",
                        Toast.LENGTH_SHORT).show()

                }

                // ...
            }
    }
}