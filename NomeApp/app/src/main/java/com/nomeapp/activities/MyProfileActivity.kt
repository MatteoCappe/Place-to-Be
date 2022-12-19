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
import com.nomeapp.models.User
import com.nomeapp.models.getMyData
import com.nomeapp.models.usernameAlreadyExists //da aggiungere in UpdateProfileActivity
import kotlinx.coroutines.*

class MyProfileActivity: AppCompatActivity() {
    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        val Username: TextView = findViewById<View>(R.id.MyProfile_Username) as TextView
        val Name: TextView = findViewById<View>(R.id.MyProfile_Name) as TextView
        val Surname: TextView = findViewById<View>(R.id.MyProfile_Surname) as TextView
        val EditProfileButton: Button = findViewById<View>(R.id.EditProfileButton) as Button
        val AddEventButton: Button = findViewById<View>(R.id.AddEventButton) as Button

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                val user: User = getMyData(this@MyProfileActivity)
                withContext(Dispatchers.Main) {
                    //TODO: immagine nello storage
                    Username.setText(user.userName)
                    Name.setText(user.Name)
                    Surname.setText(user.Surname)
                }
            }
        }

        EditProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //val intent: Intent = Intent(context, UpdateProfileActivity::class.java)
                //context.startActivity(intent)
            }
        })

        EditProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(intent)
            }
        })

    }
}