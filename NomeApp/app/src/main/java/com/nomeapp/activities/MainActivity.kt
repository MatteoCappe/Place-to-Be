package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.nomeapp.R

class MainActivity : AppCompatActivity() {
    private val TAG : String? = MainActivity::class.simpleName
    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val MyProfileButton: Button = findViewById<View>(R.id.MyProfileButton) as Button
        val ShowProfileButton: Button = findViewById<View>(R.id.ShowProfileButton) as Button
        val AddEventButton: Button = findViewById<View>(R.id.AddEventButton) as Button
        val ShowEventButton: Button = findViewById<View>(R.id.ShowEventButton) as Button

        MyProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, MyProfileActivity::class.java)
                context.startActivity(intent)
            }
        })

        ShowProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, ShowProfileActivity::class.java)
                context.startActivity(intent)
            }
        })

        AddEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(intent)
            }
        })

        ShowEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, ShowEventActivity::class.java)
                context.startActivity(intent)
            }
        })

    }


}