package com.nomeapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.models.FirebaseStorageWrapper
import com.nomeapp.models.User
import com.nomeapp.models.getUserByUsername
import kotlinx.coroutines.*
import java.io.File


class ShowProfileActivity(): AppCompatActivity() {
    private var user: User? = null
    val context: Context = this //vedi se serve
    var image: Uri? = null

    //TODO: vedi se esiste un modo per velocizzare lettura
    //TODO: colore e posizione info
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showprofile)

        var Username: String
        var Name: String
        var Surname: String

        val searched: String = "Prova" //questo dovrà essere inizializzato con una stringa
                                       //derivante dalla funzione "ricerca"
                                       //momentaneamente è inizializzato a "Prova" per far vedere come funzionerebbe

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getUserByUsername(this@ShowProfileActivity, searched)

                image = FirebaseStorageWrapper(this@ShowProfileActivity).downloadUserImage(user!!.UserID)

                withContext(Dispatchers.Main) {
                    //TODO: immagine nello storage
                    Username = user!!.userName
                    Name = user!!.Name
                    Surname = user!!.Surname

                    //si potrebbe evitare di inizializzare variabili e cambiare direttamente la view
                    findViewById<TextView>(R.id.ShowProfile_Username).text = Username
                    findViewById<TextView>(R.id.ShowProfile_Name).text = Name
                    findViewById<TextView>(R.id.ShowProfile_Surname).text = Surname
                    if (image != null) {
                        findViewById<ImageView>(R.id.ShowProfile_profileImage).setImageURI(image)
                    }
                }
            }
        }
    }

}