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
        val AddEventButton: Button = findViewById<View>(R.id.AddEventButton) as Button
        val SearchUserButton: Button = findViewById<View>(R.id.SearchUserButton) as Button
        val SearchEventButton: Button = findViewById<View>(R.id.SearchEventButton) as Button
        val FavouritesButton: Button = findViewById<View>(R.id.FavouritesButton) as Button

        MyProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, MyProfileActivity::class.java)
                context.startActivity(intent)
            }
        })

        AddEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(intent)
            }
        })

        SearchUserButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, SearchUserActivity::class.java)
                context.startActivity(intent)
            }
        })

        SearchEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, SearchEventActivity::class.java)
                context.startActivity(intent)
            }
        })

        FavouritesButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, FavouritesActivity::class.java)
                context.startActivity(intent)
            }
        })

    }


}