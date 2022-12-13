package com.nomeapp.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R

class MyProfileActivity: AppCompatActivity() {
    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        val Name: EditText = findViewById<View>(R.id.Name) as EditText
        val Surname: EditText = findViewById<View>(R.id.Surname) as EditText
        val userName: EditText = findViewById<View>(R.id.userName) as EditText



    }

}