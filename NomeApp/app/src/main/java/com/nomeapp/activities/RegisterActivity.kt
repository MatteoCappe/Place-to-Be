
package com.nomeapp.activities

//no possibilità di caricare foto al momento della registrazione siccome queste vengono salvate per userID (per comodità)
//che al momento della registrazione risulta ancora sconosciuto, si può caricare solo da updateProfileActivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.nomeapp.R
import com.nomeapp.models.*
import kotlinx.coroutines.*


class RegisterActivity : AppCompatActivity() {
    val context: Context = this

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
                    Name.setError(getString(R.string.emptyError))
                    Surname.setError(getString(R.string.emptyError))
                    userName.setError(getString(R.string.emptyError))
                    email.setError(getString(R.string.emptyError))
                    password.setError(getString(R.string.emptyError))
                    return
                }

                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            alreadyused = usernameAlreadyExists(view!!.context, userName.text.toString())
                            withContext(Dispatchers.Main) {
                                if (alreadyused) {
                                    userName.setError(getString(R.string.usernameError))
                                }
                                else {
                                    //initialize mutable list of created events
                                    var Events: MutableList<Long>? = mutableListOf()
                                    var Followers: MutableList<String>? = mutableListOf()
                                    var Following: MutableList<String>? = mutableListOf()
                                    var Favourites: MutableList<Long>? = mutableListOf()

                                    val user = User(
                                        userName.text.toString(),
                                        Name.text.toString(),
                                        Surname.text.toString(),
                                        "null",
                                        Events,
                                        Followers,
                                        Following,
                                        Favourites,
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