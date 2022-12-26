package com.nomeapp.activities


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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.nomeapp.fragments.ShowMyEventFragment
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.util.*
import java.text.SimpleDateFormat

class ShowEventActivity() : AppCompatActivity() {
    private var event: Event? = null
    val context: Context = this //vedi se serve
    var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showevent)

        //mettere casetta per tornare alla home e uscire dalla schermata di creazione dell'evento

        var Title: String
        var City: String
        var Bio: String
        var userID: String
        var isStored: Boolean = false

        val ShowCreatorButton: Button = findViewById<View>(R.id.ShowCreatorButton) as Button

        val searched: String = "Prova" //questo dovrà essere inizializzato con una stringa
                                       //derivante dalla funzione "ricerca"
                                       //momentaneamente è inizializzato a "Prova" per far vedere come funzionerebbe

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                event = getEventByTitle(this@ShowEventActivity, searched)
                /*isStored = FirebaseStorageWrapper(context).isSavedInStorage("events", event!!.eventID.toString())
                if (isStored) {*/
                    image = FirebaseStorageWrapper(this@ShowEventActivity).downloadUserImage(event!!.eventID.toString())
                //}
                withContext(Dispatchers.Main) {

                    Title = event!!.Title
                    City = event!!.City
                    Bio = event!!.Bio
                    userID = event!!.userID

                    val dateFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm")

                    val fragmentManager = supportFragmentManager

                    //si potrebbe evitare di inizializzare variabili e cambiare direttamente la view
                    findViewById<TextView>(R.id.ShowEvent_Title).text = Title
                    findViewById<TextView>(R.id.ShowEvent_City).text = City
                    findViewById<TextView>(R.id.ShowEvent_Bio).text = Bio
                    findViewById<TextView>(R.id.ShowEvent_Date).text = dateFormatter.format(event!!.Date)
                    if (image != null) {
                        findViewById<ImageView>(R.id.ShowEvent_eventImage).setImageURI(image)
                    }

                    if (userID == FirebaseAuthWrapper(context).getUid()) {
                        fragmentManager.commit {
                            setReorderingAllowed(true)
                            val frag: Fragment = ShowMyEventFragment.newInstance(event!!.eventID)
                            this.add(R.id.showMyEventFragment, frag) //vedi se add è giusto o se ci va replace
                        }
                    }

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