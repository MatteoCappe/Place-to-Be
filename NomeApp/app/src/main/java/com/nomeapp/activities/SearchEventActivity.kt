package com.nomeapp.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.nomeapp.fragments.SearchEventFragment
import java.text.SimpleDateFormat
import java.util.*

class SearchEventActivity: AppCompatActivity() {
    val fragmentManager = supportFragmentManager
    val context: Context = this
    private var myCalendar : Calendar= Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchevent)

        val SearchEventButton: Button = findViewById<View>(R.id.SearchEventButton) as Button
        val ClearDate: Button = findViewById<View>(R.id.ClearDate) as Button
        //TODO: search by città e data!!!!
        //TODO: fix problme come fatto per cerca user

        val queryTitle = intent.getStringExtra("queryTitle")
        val queryCity = intent.getStringExtra("queryCity")
        val queryDate = intent.getStringExtra("queryDate")

        val Title: EditText = findViewById<View>(R.id.searchTitle) as EditText
        val City: EditText = findViewById<View>(R.id.searchCity) as EditText
        val Date: EditText = findViewById<View>(R.id.searchDate) as EditText

        val DateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) //vedi se il formato della data va bene quando si aggiungerà ricerca
                Date.setText(dateFormat.format(myCalendar.time))
            }

        Date.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(
                    this@SearchEventActivity,
                    DateListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            }
        })

        ClearDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Date.text.clear()
            }
        })

        //risolto issue che faceva crashare app dopo una determinata serie di comandi
        if (queryTitle != null || queryCity != null  || queryDate != null ) {
            fragmentManager.commit {
                setReorderingAllowed(true)
                val frag: Fragment = SearchEventFragment.newInstance(Title.text.toString(), City.text.toString(), Date.text.toString())
                replace(R.id.SearchEventFragment, frag)
            }
            //copiato codice da sotto, proprio per risolvere l'errore
            SearchEventButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {

                    if (Title.text.isEmpty() && City.text.isEmpty() && Date.text.isEmpty()) {
                        Title.setError(getString(R.string.eventSearchError))
                        City.setError(getString(R.string.eventSearchError))
                        Date.setError(getString(R.string.eventSearchError))
                    }

                    else {
                        fragmentManager.commit {
                            setReorderingAllowed(true)
                            val frag: Fragment = SearchEventFragment.newInstance(Title.text.toString(), City.text.toString(), Date.text.toString())
                            replace(R.id.SearchEventFragment, frag)
                        }
                    }
                }
            })
        }

        SearchEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (Title.text.isEmpty() && City.text.isEmpty() && Date.text.isEmpty()) {
                    Title.setError(getString(R.string.eventSearchError))
                    City.setError(getString(R.string.eventSearchError))
                    Date.setError(getString(R.string.eventSearchError))
                }

                else {
                    fragmentManager.commit {
                        setReorderingAllowed(true)
                        val frag: Fragment = SearchEventFragment.newInstance(Title.text.toString(), City.text.toString(), Date.text.toString())
                        replace(R.id.SearchEventFragment, frag)
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        val intent: Intent = Intent(this@SearchEventActivity, MainActivity::class.java)
        startActivity(intent)
    }

}