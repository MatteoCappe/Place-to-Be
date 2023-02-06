package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.nomeapp.adapters.UsersAdapter
import com.nomeapp.models.User
import com.nomeapp.models.getUserByID
import kotlinx.coroutines.*

class FollowersActivity: AppCompatActivity() {
    var user: User? = null
    var userList: MutableList<User>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)

        val context: Context = this
        val followersList = intent.getStringArrayListExtra("Followers")

        val FollowersList: ListView = findViewById<ListView>(R.id.FollowersList)
        val FollowersEmpty: TextView = findViewById<TextView>(R.id.EmptyFollowers)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {

                if (followersList!!.isEmpty()) {
                    FollowersEmpty.setVisibility(View.VISIBLE)
                }

                for (id in followersList!!) {
                    user = getUserByID(this@FollowersActivity, id)
                    userList!!.add(user!!)
                }

                withContext(Dispatchers.Main) {
                    val adapter = UsersAdapter(this@FollowersActivity, userList!!)
                    FollowersList.adapter = adapter
                    FollowersList.onItemClickListener =
                        AdapterView.OnItemClickListener { position, view, parent, id ->
                            val UsernameFromUserBox = view.findViewById<TextView>(R.id.UserBox_Username)
                            val intent: Intent = Intent(context, ShowProfileActivity::class.java)
                            intent.putExtra("UserBoxUsername", UsernameFromUserBox.text.toString())
                            startActivity(intent)
                        }

                }
            }
        }
    }
}