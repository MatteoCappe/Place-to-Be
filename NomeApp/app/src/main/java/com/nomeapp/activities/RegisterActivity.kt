
package com.nomeapp.activities



import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.nomeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.nomeapp.models.FirebaseAuthWrapper


class RegisterActivity : AppCompatActivity() {
    val context: Context = this
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val switch: TextView = findViewById<View>(R.id.switchToLogin) as TextView
        val LoginButton: Button = findViewById<View>(R.id.registerButton) as Button

        switch.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        LoginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val userName: EditText = findViewById<View>(R.id.userName) as EditText
                val email: EditText = findViewById<View>(R.id.userEmail) as EditText
                val password: EditText = findViewById<View>(R.id.userPassword) as EditText

                if (userName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty()) {
                    userName.setError("This is required")
                    email.setError("This is required")
                    password.setError("This is required")
                    return
                }

                action(userName.text.toString(), email.text.toString(), password.text.toString())

                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                //rimanda a login dopo registration
                //poi si può mettere direttamente al main/splash ma per ora va bene cosi
            }
            //problema della mail già usata, il primo register funziona, rimane nella
            //stessa schermata, se provi altre volte ti da errore
            //fix: switch to login manuale da app
            //fix intelligente: dopo onClick del registerButton fare un intent per andare alla splash
            //oppure qualcosa tramite manifest idk

        })

    }

    fun action(userName: String, email: String, password: String) {
        val firebaseAuthWrapper: FirebaseAuthWrapper = FirebaseAuthWrapper(context)
        firebaseAuthWrapper.signUp(userName, email, password)
        //email already used
    }
}