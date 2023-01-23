package com.nomeapp.activities

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

class SearchEventActivity: AppCompatActivity() {
    val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchevent)

        val SearchEventButton: Button = findViewById<View>(R.id.SearchEventButton) as Button
        //TODO: search by città e data!!!!

        SearchEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var Title: EditText = findViewById<View>(R.id.searchTitle) as EditText

                if (Title.text.isEmpty()) {
                    Title.setError("This is required")
                }

                else {
                    fragmentManager.commit {
                        setReorderingAllowed(true)
                        val frag: Fragment = SearchEventFragment.newInstance(Title.text.toString())
                        replace(R.id.SearchEventFragment, frag)
                    }
                }
            }
        })

    }

}