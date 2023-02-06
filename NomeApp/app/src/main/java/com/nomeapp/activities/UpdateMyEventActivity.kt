package com.nomeapp.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class UpdateMyEventActivity: AppCompatActivity() {
    val context: Context = this
    var event: Event? = null
    var user: User? = null
    var eventID: Long? = null
    private var myCalendar : Calendar= Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateevent)

        eventID = intent.getLongExtra("eventID", 0)

        val Title: EditText = findViewById<View>(R.id.UpdateEvent_Title) as EditText
        val City: EditText = findViewById<View>(R.id.UpdateEvent_City) as EditText
        val Address: EditText = findViewById<View>(R.id.UpdateEvent_Address) as EditText
        val Bio: EditText = findViewById<View>(R.id.UpdateEvent_Bio) as EditText
        val Date: EditText = findViewById<View>(R.id.UpdateEvent_Date) as EditText
        val Time: EditText = findViewById<View>(R.id.UpdateEvent_Time) as EditText
        val SaveChanges: Button = findViewById<View>(R.id.UpdateEvent_SaveChanges) as Button

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                event = getEventByID(this@UpdateMyEventActivity, eventID!!)

                val DBDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val convertedDate = DBDate.parse(event!!.formattedDate!!)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(convertedDate!!)
                val formattedTime = SimpleDateFormat("HH:mm").format(convertedDate)

                withContext(Dispatchers.Main) {
                    Title.setText(event!!.Title)
                    City.setText(event!!.City)
                    Address.setText(event!!.Address)
                    Bio.setText(event!!.Bio)
                    Date.setText(formattedDate)
                    Time.setText(formattedTime)
                }
            }
        }

        val DateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                Date.setText(dateFormat.format(myCalendar.time))
            }

        Date.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(
                    this@UpdateMyEventActivity,
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
                    this@UpdateMyEventActivity,
                    { timePicker, selectedHour, selectedMinute ->
                        Time.setText("$selectedHour:$selectedMinute")
                    }, hour, minute, true
                ).show()
            }
        })

        SaveChanges.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val DBDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val convertedDate = DBDate.parse(event!!.formattedDate!!)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(convertedDate!!)

                if (Title.text.isEmpty() || Address.text.isEmpty() || City.text.isEmpty()
                    || Bio.text.isEmpty() || Date.text.isEmpty() || Time.text.isEmpty()) {
                    Title.setError(getString(R.string.emptyError))
                    City.setError(getString(R.string.emptyError))
                    Address.setError(getString(R.string.emptyError))
                    Bio.setError(getString(R.string.emptyError))
                    Date.setError(getString(R.string.emptyError))
                    Time.setError(getString(R.string.emptyError))
                    return
                }

                else if (myCalendar.timeInMillis < System.currentTimeMillis() &&
                        !Date.text.toString().equals(formattedDate)) {
                    Date.setError("Non puoi scegliere un giorno già passato!")
                }

                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            user = getMyData(this@UpdateMyEventActivity)

                            withContext(Dispatchers.Main) {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                                val EventDate = formatter.parse(Date.text.toString() + " " + Time.text.toString())

                                val event = Event(
                                    Title.text.toString(),
                                    eventID!!,
                                    EventDate!!,
                                    City.text.toString(),
                                    Address.text.toString(),
                                    Bio.text.toString(),
                                    user!!.UserID,
                                    user!!.userName
                                )

                                FirebaseDbWrapper(this@UpdateMyEventActivity).writeDbEvent(event, eventID!!)

                                val returnToProfile: Intent = Intent(context, MyProfileActivity::class.java)
                                context.startActivity(returnToProfile)
                            }
                        }
                    }
                }
            }
        })
    }

}