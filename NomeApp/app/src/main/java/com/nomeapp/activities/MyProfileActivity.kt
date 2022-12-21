package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.models.User
import com.nomeapp.models.getMyData
import com.nomeapp.models.usernameAlreadyExists //da aggiungere in UpdateProfileActivity
import kotlinx.coroutines.*

class MyProfileActivity: AppCompatActivity() {
    private var user: User? = null
    val context: Context = this //vedi se serve

    //TODO: vedi se esiste un modo per velocizzare lettura
    //TODO: colore e posizione info
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        var Username: String
        var Name: String
        var Surname: String
        var userID: String

        //non entra nella coroutine
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@MyProfileActivity)
                withContext(Dispatchers.Main) {
                    //TODO: immagine nello storage
                    Username = user!!.userName
                    Name = user!!.Name
                    Surname = user!!.Surname
                    userID = user!!.UserID

                    findViewById<TextView>(R.id.MyProfile_Username).text = Username
                    findViewById<TextView>(R.id.MyProfile_Name).text = Name
                    findViewById<TextView>(R.id.MyProfile_Surname).text = Surname
                }
            }
        }

        val EditProfileButton: FloatingActionButton = findViewById<View>(R.id.MyProfile_EditProfileButton) as FloatingActionButton
        val AddEventButton: FloatingActionButton = findViewById<View>(R.id.MyProfile_AddEventButton) as FloatingActionButton

        /*EditProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //vedi se serve userID
                val intent: Intent = Intent(context, UpdateProfileActivity::class.java)
                context.startActivity(intent)
            }
        })*/

        AddEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //vedi se serve userID
                val intent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(intent)
            }
        })
    }

}