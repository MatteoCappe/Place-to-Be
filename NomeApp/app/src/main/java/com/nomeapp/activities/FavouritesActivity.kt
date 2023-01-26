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
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.*
import kotlinx.coroutines.*

class FavouritesActivity: AppCompatActivity() {
    var event: Event? = null
    var user: User? = null
    var eventList: MutableList<Event>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        val context: Context = this

        val FavouritesList: ListView = findViewById<ListView>(R.id.FavouritesList)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@FavouritesActivity)

                if (user!!.Favourites!!.size == 0) {
                    val intent: Intent = Intent(context, FavouritesNotFoundActivity::class.java)
                    context.startActivity(intent)
                } //TODO: check

                for (id in user!!.Favourites!!) { //boh vedi per !!
                    event = getEventByID(this@FavouritesActivity, id)
                    eventList!!.add(event!!)
                }

                withContext(Dispatchers.Main) {
                    val adapter = EventsAdapter(this@FavouritesActivity, eventList!!)
                    FavouritesList.adapter = adapter
                    FavouritesList.onItemClickListener =
                        AdapterView.OnItemClickListener { position, view, parent, id ->
                            val EventIDFromBox: Long =
                                view.findViewById<TextView>(R.id.EventBox_ID).text.toString().toLong()
                            val intent: Intent = Intent(context, ShowEventActivity::class.java)
                            intent.putExtra("EventBoxID", EventIDFromBox)
                            startActivity(intent)
                        }

                }
            }
        }
    }
}