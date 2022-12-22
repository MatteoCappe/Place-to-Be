
package com.nomeapp.activities

//no possibilità di caricare foto al momento della registrazione siccome queste vengono salvate per userID (per comodità)
//che al momento della registrazione risulta ancora sconosciuto, si può caricare solo da updateProfileActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.nomeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.nomeapp.models.*
import kotlinx.coroutines.*


class RegisterActivity : AppCompatActivity() {
    val context: Context = this
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val switch: TextView = findViewById<View>(R.id.switchToLogin) as TextView
        val registerButton: Button = findViewById<View>(R.id.registerButton) as Button

        var alreadyused: Boolean = false

        switch.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val Name: EditText = findViewById<View>(R.id.Name) as EditText
                val Surname: EditText = findViewById<View>(R.id.Surname) as EditText
                val userName: EditText = findViewById<View>(R.id.userName) as EditText
                val email: EditText = findViewById<View>(R.id.userEmail) as EditText
                val password: EditText = findViewById<View>(R.id.userPassword) as EditText

                if (Name.text.isEmpty() || Surname.text.isEmpty() || userName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty()) {
                    Name.setError("This is required")
                    Surname.setError("This is required")
                    userName.setError("This is required")
                    email.setError("This is required")
                    password.setError("This is required")
                    return
                }

                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            alreadyused = usernameAlreadyExists(view!!.context, userName.text.toString())
                            withContext(Dispatchers.Main) {
                                if (alreadyused) {
                                    userName.setError("This username is already in use")
                                }
                                else {
                                    val user = User(
                                        userName.text.toString(),
                                        Name.text.toString(),
                                        Surname.text.toString(),
                                        "null"
                                    )

                                    action(user, email.text.toString(), password.text.toString())
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun action(user: User, email: String, password: String) {
        val firebaseAuthWrapper: FirebaseAuthWrapper = FirebaseAuthWrapper(context)
        firebaseAuthWrapper.signUp(user, email, password)
    }
}