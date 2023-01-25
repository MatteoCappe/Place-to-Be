package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.nomeapp.adapters.UsersAdapter
import com.nomeapp.models.User
import com.nomeapp.models.getUserByID
import kotlinx.coroutines.*

class FollowingActivity: AppCompatActivity() {
    var user: User? = null
    var userList: MutableList<User>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        val context: Context = this
        val followingList = intent.getStringArrayListExtra("Following")

        val FollowingList: ListView = findViewById<ListView>(R.id.FollowingList)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {

                for (id in followingList!!) {
                    user = getUserByID(this@FollowingActivity, id)
                    userList!!.add(user!!)
                }

                withContext(Dispatchers.Main) {
                    val adapter = UsersAdapter(this@FollowingActivity, userList!!)
                        FollowingList.adapter = adapter
                        FollowingList.onItemClickListener =
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