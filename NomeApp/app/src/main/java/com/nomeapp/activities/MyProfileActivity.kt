package com.nomeapp.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.nomeapp.R
import com.nomeapp.models.User
import com.nomeapp.models.getMyData

class MyProfileActivity: AppCompatActivity() {
    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        val context: Context = this

        val Name: EditText = findViewById<View>(R.id.Name) as EditText
        val Surname: EditText = findViewById<View>(R.id.Surname) as EditText
        val userName: EditText = findViewById<View>(R.id.userName) as EditText

        val user: User = getMyData(context)

        userName.setText(user.userName)
        Name.setText(user.Name)
        Surname.setText(user.Surname)

    }

}