package com.nomeapp.activities


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.nomeapp.models.Event
import com.nomeapp.models.FirebaseAuthWrapper
import com.nomeapp.models.FirebaseDbWrapper
import com.nomeapp.models.User

class AddEventActivity : AppCompatActivity() {
    val context: Context = this
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addevent)

        val CreateEvent: Button = findViewById<View>(R.id.CreateEvent) as Button

        CreateEvent.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val Title: EditText = findViewById<View>(R.id.EventTitle) as EditText
                val City: EditText = findViewById<View>(R.id.City) as EditText
                val Bio: EditText = findViewById<View>(R.id.Bio) as EditText
                val Date: DatePicker = findViewById<View>(R.id.Date) as DatePicker
                val Time: TimePicker = findViewById<View>(R.id.Time) as TimePicker

                //guarda come fare check su data e ora
                if (Title.text.isEmpty() || City.text.isEmpty() || Bio.text.isEmpty()) {
                    Title.setError("This is required")
                    City.setError("This is required")
                    Bio.setError("This is required")
                    return
                }

                val userID = FirebaseAuthWrapper(this@AddEventActivity).getUid().toString()

                val event = Event(
                    Title.text.toString(),
                    City.text.toString(),
                    Bio.text.toString(),
                    userID
                )

                FirebaseDbWrapper(this@AddEventActivity).writeDbEvent(event)
            }
        })

    }
}