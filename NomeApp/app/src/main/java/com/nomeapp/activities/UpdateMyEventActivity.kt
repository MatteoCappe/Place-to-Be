package com.nomeapp.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateMyEventActivity: AppCompatActivity() {
    val context: Context = this
    var image: Uri? = null
    var event: Event? = null
    var user: User? = null
    var eventID: Long? = null
    private var myCalendar : Calendar= Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateevent)

        eventID = intent.getLongExtra("eventID", 0)

        var alreadyused: Boolean = false

        val Title: EditText = findViewById<View>(R.id.UpdateEvent_Title) as EditText
        val City: EditText = findViewById<View>(R.id.UpdateEvent_City) as EditText
        val Bio: EditText = findViewById<View>(R.id.UpdateEvent_Bio) as EditText
        val Date: EditText = findViewById<View>(R.id.UpdateEvent_Date) as EditText
        val Time: EditText = findViewById<View>(R.id.UpdateEvent_Time) as EditText
        val UploadImage: Button = findViewById<View>(R.id.UpdateEvent_UploadImage) as Button
        val SaveChanges: Button = findViewById<View>(R.id.UpdateEvent_SaveChanges) as Button

        UploadImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //TODO: immagine rettangolare obbligatoria per evento?
                UploadImage.setOnClickListener {
                    ImagePicker.with(this@UpdateMyEventActivity)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start()
                }
            }
        })

        val DateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                //TODO: mettere un check in modo che la data inserita per l'evento sia maggiore di quella attuale

                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US) //vedi se il formato della data va bene quando si fa la ricerca
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

                //TODO: if campo = vuoto, rimettere info di prima, oppure mettere a schermo le info vecchie e poi modificarle
                if (Title.text.isEmpty() || City.text.isEmpty() || Bio.text.isEmpty()) {
                    Title.setError("This is required")
                    City.setError("This is required")
                    Bio.setError("This is required")
                    return
                }

                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            //momentaneo, poi si potranno avere più eventi con lo stesso nome
                            alreadyused = titleAlreadyExists(view!!.context, Title.text.toString())
                            user = getMyData(this@UpdateMyEventActivity)

                            withContext(Dispatchers.Main) {
                                if (alreadyused) {
                                    Title.setError("This title is already in use")
                                }
                                else {
                                    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
                                    val EventDate = formatter.parse(Date.text.toString() + " " + Time.text.toString())

                                    val event = Event(
                                        Title.text.toString(),
                                        eventID!!,
                                        EventDate,
                                        City.text.toString(),
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
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                image = data?.data!!
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT).show()
            }
        }
        if(image != null) {
            FirebaseStorageWrapper(this@UpdateMyEventActivity).uploadEventImage(image!!, eventID!!.toString())
        }
    }

}