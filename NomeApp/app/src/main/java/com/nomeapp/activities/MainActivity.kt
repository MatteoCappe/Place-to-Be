package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nomeapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.nomeapp.models.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    val context: Context = this

    lateinit var toggle: ActionBarDrawerToggle
    var user: User? = null
    var image: Uri? = null
    var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ///////////////////////////////////////MENU///////////////////////////////////////////
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                email = FirebaseAuthWrapper(context).getEmail()
                user = getMyData(context)
                image = FirebaseStorageWrapper(context).downloadUserImage(user!!.UserID)

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.NavMenu_Username).text = user!!.userName
                    findViewById<TextView>(R.id.NavMenu_Email).text = email!!

                    if (image != null) {
                        findViewById<ImageView>(R.id.NavMenu_Photo).setImageURI(image)
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

        //get user token for notifiaction
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                email = FirebaseAuthWrapper(context).getEmail()
                user = getMyData(context)

                withContext(Dispatchers.Main) {

                    FirebaseMessagingWrapper.sharedPref =
                        getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

                    FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                        if (result != null) {
                            user!!.FBToken = result
                            FirebaseDbWrapper(context).writeDbUser(user!!)
                        }
                    }
                }
            }
        }

        val MyProfileButton: RelativeLayout = findViewById<View>(R.id.MyProfile) as RelativeLayout
        val AddEventButton: RelativeLayout = findViewById<View>(R.id.AddEvent) as RelativeLayout
        val SearchButton: RelativeLayout = findViewById<View>(R.id.Search) as RelativeLayout
        val FavouritesButton: RelativeLayout = findViewById<View>(R.id.Favourites) as RelativeLayout

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

        SearchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, SearchActivity::class.java)
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

    ///////////////////////////////////////MENU///////////////////////////////////////////
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    ///////////////////////////////////////MENU///////////////////////////////////////////

}