package com.nomeapp.activities


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.util.*
import java.text.SimpleDateFormat

class ShowEventActivity : AppCompatActivity() {
    private var event: Event? = null
    val context: Context = this //vedi se serve
    var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showevent)

        //mettere casetta per toranare alla home e uscire dalla schermata di creazione dell'evento

        var Title: String
        var City: String
        var Bio: String
        //date e time?

        val ShowCreatorButton: Button = findViewById<View>(R.id.ShowCreatorButton) as Button

        val searched: String = "Prova" //questo dovrà essere inizializzato con una stringa
                                       //derivante dalla funzione "ricerca"
                                       //momentaneamente è inizializzato a "Prova" per far vedere come funzionerebbe

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                event = getEventByTitle(this@ShowEventActivity, searched)
                //image = FirebaseStorageWrapper(this@ShowEventActivity).downloadEventImage(event!!.eventID.toString())

                withContext(Dispatchers.Main) {
                    //TODO: immagine nello storage
                    Title = event!!.Title
                    City = event!!.City
                    Bio = event!!.Bio

                    val dateFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm")

                    //si potrebbe evitare di inizializzare variabili e cambiare direttamente la view
                    findViewById<TextView>(R.id.ShowEvent_Title).text = Title
                    findViewById<TextView>(R.id.ShowEvent_City).text = City
                    findViewById<TextView>(R.id.ShowEvent_Bio).text = Bio
                    findViewById<TextView>(R.id.ShowEvent_Date).text = dateFormatter.format(event!!.Date)
                    /*if (image != null) {
                        findViewById<ImageView>(R.id.ShowEvent_eventImage).setImageURI(image)
                    }*/

                    //questo al momento rimanderà al profilo prova, ma verrà poi inizializzato
                    //in modo da rimandare al profilo di chi ha creato l'evento
                    ShowCreatorButton.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            val intent: Intent = Intent(context, ShowProfileActivity::class.java)
                            context.startActivity(intent)
                        }
                    })
                }
            }
        }
    }
    //upload image disponibile solo dal menu di modifica dell'evento
    //stesso problema del registration, non posso sapere id prima di ottenerlo
    //vedi se magari dopo CreateEvent click mettere un intent che porta ad aggiungere la foto (?)
    //oppure se si possa mettere la ImageView e fare in modo di leggerla e caricarla dopo averla inserita nel solito modo
}