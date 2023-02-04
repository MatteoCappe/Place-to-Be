package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.nomeapp.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.nomeapp.adapters.ViewPagerAdapter
import com.nomeapp.models.FirebaseAuthWrapper
import com.nomeapp.models.FirebaseStorageWrapper
import com.nomeapp.models.User
import com.nomeapp.models.getMyData
import kotlinx.coroutines.*

class SearchActivity: AppCompatActivity() {

    val context: Context = this

    lateinit var toggle: ActionBarDrawerToggle
    var user: User? = null
    var image: Uri? = null
    var email: String? = null

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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

        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) {tab, position ->
            when(position) {
                0 -> {
                    tab.text="UTENTI"
                }
                1 -> {
                    tab.text="EVENTI"
                }
            }
        }.attach()
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