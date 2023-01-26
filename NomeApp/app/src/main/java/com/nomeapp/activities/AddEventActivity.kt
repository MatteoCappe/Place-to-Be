package com.nomeapp.activities


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.util.*
import java.text.SimpleDateFormat

class AddEventActivity : AppCompatActivity() {
    val context: Context = this
    private var myCalendar : Calendar= Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addevent)

        val Title: EditText = findViewById<View>(R.id.EventTitle) as EditText
        val City: EditText = findViewById<View>(R.id.City) as EditText
        val Address: EditText = findViewById<View>(R.id.Address) as EditText
        val Bio: EditText = findViewById<View>(R.id.Bio) as EditText
        val Date: EditText = findViewById<View>(R.id.EventDate) as EditText
        val Time: EditText = findViewById<View>(R.id.EventTime) as EditText
        val CreateEvent: Button = findViewById<View>(R.id.CreateEvent) as Button

        val DateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                //TODO: mettere un check in modo che la data inserita per l'evento sia maggiore di quella attuale

                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US) //vedi se il formato della data va bene quando si aggiungerà ricerca
                Date.setText(dateFormat.format(this.myCalendar.time))
            }

        Date.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(
                    this@AddEventActivity,
                    DateListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            }
        })

        Time.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                val currentTime = Calendar.getInstance()
                val hour = currentTime[Calendar.HOUR_OF_DAY]
                val minute = currentTime[Calendar.MINUTE]

                TimePickerDialog(
                    this@AddEventActivity,
                    { timePicker, selectedHour, selectedMinute ->
                        Time.setText("$selectedHour:$selectedMinute")
                    }, hour, minute, true
                ).show()
            }
        })

        CreateEvent.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //TODO: check su data e ora?
                if (Title.text.isEmpty() || City.text.isEmpty() || Bio.text.isEmpty()) {
                    Title.setError("This is required")
                    City.setError("This is required")
                    Bio.setError("This is required")
                    return
                }
                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            val user: User = getMyData(this@AddEventActivity)

                            //eventID, parte da 0 a ogni evento creato incrementa di 1
                            val eventID: Long = getEventID(this@AddEventActivity)

                            withContext(Dispatchers.Main) {
                                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                                val EventDate = formatter.parse(Date.text.toString() + " " + Time.text.toString())

                                val event = Event(
                                    Title.text.toString(),
                                    eventID,
                                    EventDate,
                                    City.text.toString(),
                                    Address.text.toString(),
                                    Bio.text.toString(),
                                    user.UserID,
                                    user.userName
                                )

                                //add evento a mutable list
                                user.Events!!.add(eventID)
                                FirebaseDbWrapper(this@AddEventActivity).writeDbUser(user)

                                FirebaseDbWrapper(this@AddEventActivity).writeDbEvent(event, eventID)
                            }
                        }
                    }

                    //torna alla home page, vedi se si può invece riportare all'activity precedente
                    //siccome a Add Event si può accedere sia da profilo che da home
                    val intent: Intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
    //upload image disponibile solo dal menu di modifica dell'evento
    //stesso problema del registration, non posso sapere id prima di ottenerlo
    //vedi se magari dopo CreateEvent dopo click mettere un intent che porta ad aggiungere la foto (?)
    //oppure se si possa mettere la ImageView e fare in modo di leggerla e caricarla dopo averla inserita nel solito modo
}