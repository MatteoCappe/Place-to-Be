package com.nomeapp.activities

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
import com.nomeapp.fragments.SearchUserFragment

class SearchUserActivity: AppCompatActivity() {
    val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchuser)

        val SearchUserButton: Button = findViewById<View>(R.id.SearchUserButton) as Button

        val searched = intent.getStringExtra("query")

        //risolto issue che faceva crashare app dopo una determinata serie di comandi
        if (searched != null) {
            fragmentManager.commit {
                setReorderingAllowed(true)
                val frag: Fragment = SearchUserFragment.newInstance(searched)
                replace(R.id.SearchUserFragment, frag)
            }
            //copiato codice da sotto, proprio per risolvere l'errore
            SearchUserButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    var userName: EditText = findViewById<View>(R.id.searchUserName) as EditText

                    if (userName.text.isEmpty()) {
                        userName.setError(getString(R.string.emptyError))
                    }

                    else {
                        fragmentManager.commit {
                            setReorderingAllowed(true)
                            val frag: Fragment = SearchUserFragment.newInstance(userName.text.toString())
                            replace(R.id.SearchUserFragment, frag)
                        }
                    }
                }
            })
        }

        else {
            SearchUserButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    var userName: EditText = findViewById<View>(R.id.searchUserName) as EditText

                    if (userName.text.isEmpty()) {
                        userName.setError("This is required")
                    }

                    else {
                        fragmentManager.commit {
                            setReorderingAllowed(true)
                            val frag: Fragment = SearchUserFragment.newInstance(userName.text.toString())
                            replace(R.id.SearchUserFragment, frag)
                        }
                    }
                }
            })
        }
    }

    override fun onBackPressed() {
        val intent: Intent = Intent(this@SearchUserActivity, MainActivity::class.java)
        startActivity(intent)
    }

}