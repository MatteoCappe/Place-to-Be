package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nomeapp.R
import com.google.android.material.navigation.NavigationView
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.*
import kotlinx.coroutines.*

class FavouritesActivity: AppCompatActivity() {
    var event: Event? = null
    var user: User? = null
    var eventList: MutableList<Event>? = arrayListOf()

    val context: Context = this
    var userMenu: User? = null
    var imageMenu: Uri? = null
    var email: String? = null

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        ///////////////////////////////////////MENU///////////////////////////////////////////
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                email = FirebaseAuthWrapper(context).getEmail()
                userMenu = getMyData(context)
                imageMenu = FirebaseStorageWrapper(context).downloadUserImage(userMenu!!.UserID)

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.NavMenu_Username).text = userMenu!!.userName
                    findViewById<TextView>(R.id.NavMenu_Email).text = email!!

                    if (imageMenu != null) {
                        findViewById<ImageView>(R.id.NavMenu_Photo).setImageURI(imageMenu)
                    }
                }
            }
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> intent = Intent(context, MainActivity::class.java)
                R.id.nav_myprofile -> intent = Intent(context, MyProfileActivity::class.java)
                R.id.nav_addevent -> intent = Intent(context, AddEventActivity::class.java)
                R.id.nav_search -> intent = Intent(context, SearchActivity::class.java)
                R.id.nav_favourites -> intent = Intent(context, FavouritesActivity::class.java)
                R.id.nav_logout -> intent = Intent(context, LoginActivity::class.java)
            }

            context.startActivity(intent)
            true
        }
        ///////////////////////////////////////MENU///////////////////////////////////////////

        val FavouritesList: ListView = findViewById<ListView>(R.id.FavouritesList)
        val FavouritesEmpty: TextView = findViewById<TextView>(R.id.EmptyFavourites)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@FavouritesActivity)

                if (user!!.Favourites!!.size == 0) {
                    FavouritesEmpty.setVisibility(View.VISIBLE)
                }
                else {
                    for (id in user!!.Favourites!!) {
                        event = getEventByID(this@FavouritesActivity, id)
                        eventList!!.add(event!!)
                    }

                    withContext(Dispatchers.Main) {
                        val adapter = EventsAdapter(this@FavouritesActivity, eventList!!)
                        FavouritesList.adapter = adapter
                        FavouritesEmpty.setVisibility(View.GONE)
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

    ///////////////////////////////////////MENU///////////////////////////////////////////
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    ///////////////////////////////////////MENU///////////////////////////////////////////
}