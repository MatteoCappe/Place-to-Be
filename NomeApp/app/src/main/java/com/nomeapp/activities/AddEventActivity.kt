package com.nomeapp.activities


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nomeapp.R
import com.google.android.material.navigation.NavigationView
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.util.*
import java.text.SimpleDateFormat

class AddEventActivity : AppCompatActivity() {
    val context: Context = this
    private var myCalendar : Calendar= Calendar.getInstance()
    var userMenu: User? = null
    var imageMenu: Uri? = null
    var email: String? = null

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addevent)

        ///////////////////////////////////////MENU///////////////////////////////////////////
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                email = FirebaseAuthWrapper(context).getEmail()
                userMenu = getMyData(context)
                imageMenu = FirebaseStorageWrapper(context).downloadUserImage(userMenu!!.UserID)

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.NavMenu_Username).text = userMenu!!.userName
                    findViewById<TextView>(R.id.NavMenu_Email).text = email!!

                    if (imageMenu != null) {
                        findViewById<ImageView>(R.id.NavMenu_Photo).setImageURI(imageMenu)
                    }
                }
            }
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> intent = Intent(context, MainActivity::class.java)
                R.id.nav_myprofile -> intent = Intent(context, MyProfileActivity::class.java)
                R.id.nav_addevent -> intent = Intent(context, AddEventActivity::class.java)
                R.id.nav_search -> intent = Intent(context, SearchActivity::class.java)
                R.id.nav_favourites -> intent = Intent(context, FavouritesActivity::class.java)
                R.id.nav_logout -> intent = Intent(context, LoginActivity::class.java)
            }

            context.startActivity(intent)
            true
        }
        ///////////////////////////////////////MENU///////////////////////////////////////////

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

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) //vedi se il formato della data va bene quando si aggiungerà ricerca
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
                if (Title.text.isEmpty() || City.text.isEmpty() || Address.text.isEmpty()
                    || Bio.text.isEmpty() || Date.text.isEmpty() || Time.text.isEmpty()) {
                    Title.setError(getString(R.string.emptyError))
                    City.setError(getString(R.string.emptyError))
                    Address.setError(getString(R.string.emptyError))
                    Bio.setError(getString(R.string.emptyError))
                    Date.setError(getString(R.string.emptyError))
                    Time.setError(getString(R.string.emptyError))
                    return
                }
                else if (myCalendar.timeInMillis < System.currentTimeMillis() - 86400000) { //TODO: && check su ora?
                    Date.setError("Non puoi scegliere un giorno già passato!")
                }
                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            val user: User = getMyData(this@AddEventActivity)

                            //eventID, parte da 0 a ogni evento creato incrementa di 1
                            val eventID: Long = getEventID(this@AddEventActivity)

                            withContext(Dispatchers.Main) {
                                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
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

                                user.Events!!.add(eventID)
                                FirebaseDbWrapper(this@AddEventActivity).writeDbUser(user)

                                FirebaseDbWrapper(this@AddEventActivity).writeDbEvent(event, eventID)
                            }
                        }
                    }

                    val intent: Intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

    ///////////////////////////////////////MENU///////////////////////////////////////////
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    ///////////////////////////////////////MENU///////////////////////////////////////////

}