
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


class LoginActivity : AppCompatActivity() {
    val context : Context = this
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val switch: TextView = findViewById<View>(R.id.switchToRegister) as TextView
        val LoginButton: Button = findViewById<View>(R.id.loginButton) as Button

        switch.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        LoginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email: EditText = findViewById<View>(R.id.userEmail) as EditText
                val password: EditText = findViewById<View>(R.id.userPassword) as EditText

                if (email.text.isEmpty() || password.text.isEmpty()) {
                    email.setError("This is required")
                    password.setError("This is required")
                    return
                }

                action(email.text.toString(), password.text.toString())
            }
        })

    }

    fun action(email: String, password: String) {
        val firebaseAuthWrapper : FirebaseAuthWrapper = FirebaseAuthWrapper(context)
        firebaseAuthWrapper.signIn(email, password)
    }

}


