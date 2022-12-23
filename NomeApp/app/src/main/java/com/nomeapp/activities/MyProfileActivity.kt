package com.nomeapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.models.FirebaseStorageWrapper
import com.nomeapp.models.User
import com.nomeapp.models.getMyData
import kotlinx.coroutines.*

class MyProfileActivity: AppCompatActivity() {
    //mettere casetta che rimanda alla home page (vdi fab)
    //var profileImage: ImageView? = null
    private var user: User? = null
    var image: Uri? = null

    //TODO: vedi se esiste un modo per velocizzare lettura
    //TODO: colore e posizione info
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)
        val context: Context = this


        var Username: String
        var Name: String
        var Surname: String
        var userID: String

        //read user data
        //metti fun per dire che se firebasestorage nome image == uid false -> carica omino vuoto??, non dovrebbe servire
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@MyProfileActivity)
                image = FirebaseStorageWrapper(this@MyProfileActivity).downloadUserImage(user!!.UserID)

                withContext(Dispatchers.Main) {
                    Username = user!!.userName
                    Name = user!!.Name
                    Surname = user!!.Surname
                    userID = user!!.UserID

                    findViewById<TextView>(R.id.MyProfile_Username).text = Username
                    findViewById<TextView>(R.id.MyProfile_Name).text = Name
                    findViewById<TextView>(R.id.MyProfile_Surname).text = Surname
                    if (image != null) {
                        findViewById<ImageView>(R.id.MyProfile_ProfileImageButton).setImageURI(image)
                    }
                    //else -> omino profilo vuoto?, non credo servirà
                }
            }
        }

        val EditProfileButton: FloatingActionButton = findViewById<View>(R.id.MyProfile_EditProfileButton) as FloatingActionButton
        val AddEventButton: FloatingActionButton = findViewById<View>(R.id.MyProfile_AddEventButton) as FloatingActionButton
        val ProfileImageButton: ImageButton = findViewById<View>(R.id.MyProfile_ProfileImageButton) as ImageButton

        EditProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val update: Intent = Intent(context, UpdateProfileActivity::class.java)
                context.startActivity(update)
            }
        })

        AddEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val addEvent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(addEvent)
            }
        })

        ProfileImageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val addEvent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(addEvent)
            }
        })
    }

}