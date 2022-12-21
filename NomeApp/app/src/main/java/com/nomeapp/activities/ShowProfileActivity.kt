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
import com.example.nomeapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.models.User
import com.nomeapp.models.getUserByUsername
import kotlinx.coroutines.*

class ShowProfileActivity: AppCompatActivity() {
    private var user: User? = null
    val context: Context = this //vedi se serve
    /*var profileImage: ImageView? = null
    var image: Uri? = null*/

    //TODO: vedi se esiste un modo per velocizzare lettura
    //TODO: colore e posizione info
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showprofile)

        val profileImage: ImageView = findViewById<View>(R.id.ShowProfile_profileImage) as ImageView
        val ShowProfileUploadImage: Button = findViewById<View>(R.id.ShowProfile_UploadImage) as Button

        /*ShowProfileUploadImage.setOnClickListener {
            ImagePicker.with(this@ShowProfileActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }*/
        //qua serve solo download image from firebase storage

        var Username: String
        var Name: String
        var Surname: String

        val searched: String = "Prova" //questo dovrà essere inizializzato con una stringa
                                       //derivante dalla funzione "ricerca"
                                       //momentaneamente è inizializzato a "Prova" per far vedere come funzionerebbe

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getUserByUsername(this@ShowProfileActivity, searched)
                withContext(Dispatchers.Main) {
                    //TODO: immagine nello storage
                    Username = user!!.userName
                    Name = user!!.Name
                    Surname = user!!.Surname

                    Log.e("username", Username)

                    findViewById<TextView>(R.id.ShowProfile_Username).text = Username
                    findViewById<TextView>(R.id.ShowProfile_Name).text = Name
                    findViewById<TextView>(R.id.ShowProfile_Surname).text = Surname
                }
            }
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                image = data?.data!!
                profileImage!!.setImageURI(image)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
    //qua ci va solo download poi correggi

}