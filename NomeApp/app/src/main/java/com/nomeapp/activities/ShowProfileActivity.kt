package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.nomeapp.R
import com.google.android.material.navigation.NavigationView
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowProfileActivity(): AppCompatActivity() {
    val TAG = "ShowProfileActivity"

    private var user: User? = null
    private var currentUser: User? = null
    private var event: Event? = null
    var image: Uri? = null
    var eventList: MutableList<Event>? = arrayListOf()

    val context: Context = this
    var userMenu: User? = null
    var imageMenu: Uri? = null
    var email: String? = null
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showprofile)

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

        val FollowUnfollow: Button = findViewById<View>(R.id.ShowProfile_FollowUnfollowButton) as Button
        val Followers = findViewById<View>(R.id.ShowProfile_ViewFollowers) as LinearLayout
        val Following = findViewById<View>(R.id.ShowProfile_ViewFollowing) as LinearLayout
        val ListOfEvents = findViewById<View>(R.id.ShowProfile_EventList) as ListView

        val userID: String? = FirebaseAuthWrapper(this@ShowProfileActivity).getUid()
        val searched = intent.getStringExtra("UserBoxUsername")!!

        val currentTime = Calendar.getInstance()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getUserByUsername(this@ShowProfileActivity, searched)
                currentUser = getMyData(this@ShowProfileActivity)
                image = FirebaseStorageWrapper(this@ShowProfileActivity).downloadUserImage(user!!.UserID)

                val ArrayListEvents: ArrayList<Long> = ArrayList(user!!.Events!!)
                val ArrayListFollowers: ArrayList<String> = ArrayList(user!!.Followers!!)
                val ArrayListFollowing: ArrayList<String> = ArrayList(user!!.Following!!)

                if (user!!.Events!!.size != 0) {
                    for (id in ArrayListEvents) {
                        event = getEventByID(this@ShowProfileActivity, id)
                        if (LocalDateTime.parse(event!!.formattedDate, formatter).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() > currentTime.timeInMillis) {
                            eventList!!.add(event!!)
                        }
                    }
                }

                if (user!!.UserID == userID!!) {
                    val intent: Intent = Intent(context, MyProfileActivity::class.java)
                    startActivity(intent)
                }

                if (currentUser!!.Following!!.contains(user!!.UserID)) {
                    FollowUnfollow.text = getString(R.string.unfollow)
                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                }
                else {
                    FollowUnfollow.text = getString(R.string.follow)
                    FollowUnfollow.setBackgroundColor(Color.parseColor("#FF6200EE"))
                }

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.ShowProfile_Username).text = user!!.userName
                    findViewById<TextView>(R.id.ShowProfile_Name).text = user!!.Name
                    findViewById<TextView>(R.id.ShowProfile_Surname).text = user!!.Surname
                    findViewById<TextView>(R.id.ShowProfile_Followers).text = user!!.Followers!!.size.toString()
                    findViewById<TextView>(R.id.ShowProfile_Following).text = user!!.Following!!.size.toString()

                    if (image != null) {
                        findViewById<ImageView>(R.id.ShowProfile_profileImage).setImageURI(image)
                    }

                    val adapter = EventsAdapter(this@ShowProfileActivity, eventList!!)
                    ListOfEvents.adapter = adapter
                    ListOfEvents.onItemClickListener =
                        AdapterView.OnItemClickListener { position, view, parent, id ->
                            val EventIDFromBox: Long =
                                view.findViewById<TextView>(R.id.EventBox_ID).text.toString().toLong()
                            val intent: Intent = Intent(context, ShowEventActivity::class.java)
                            intent.putExtra("EventBoxID", EventIDFromBox)
                            startActivity(intent)
                        }

                    FollowUnfollow.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            if (FollowUnfollow.text == getString(R.string.unfollow)) {
                                currentUser!!.Following!!.remove(user!!.UserID)
                                FirebaseDbWrapper(this@ShowProfileActivity).writeDbUser(currentUser!!)

                                user!!.Followers!!.remove(userID!!)
                                FirebaseDbWrapper(this@ShowProfileActivity).writeDbShownUser(user!!)

                                //update numero followers
                                findViewById<TextView>(R.id.ShowProfile_Followers).text = (user!!.Followers!!.size).toString()

                                ArrayListFollowers.remove(userID!!)

                                FollowUnfollow.text = getString(R.string.follow)
                                FollowUnfollow.setBackgroundColor(Color.parseColor("#FF6200EE"))
                            }
                            else {
                                if (!currentUser!!.Following!!.contains(user!!.UserID)) {
                                    currentUser!!.Following!!.add(user!!.UserID)
                                    FirebaseDbWrapper(this@ShowProfileActivity).writeDbUser(currentUser!!)

                                    FollowUnfollow.text = getString(R.string.unfollow)
                                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                                }

                                if (!user!!.Followers!!.contains(userID!!)) {
                                    user!!.Followers!!.add(userID!!)
                                    FirebaseDbWrapper(this@ShowProfileActivity).writeDbShownUser(user!!)

                                    //update numero followers
                                    findViewById<TextView>(R.id.ShowProfile_Followers).text = (user!!.Followers!!.size).toString()

                                    ArrayListFollowers.add(userID!!)

                                    FollowUnfollow.text = getString(R.string.unfollow)
                                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                                }

                                //invio notifica
                                val title = "Follower"
                                val username = currentUser!!.userName
                                val message = "$username ha iniziato a seguirti!"
                                PushNotification(
                                    NotificationData(title, message),
                                    user!!.FBToken!!
                                ).also {
                                    sendNotification(it)
                                }
                            }
                        }
                    })

                    Followers.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(view: View?) {
                            val intent: Intent = Intent(context, FollowersActivity::class.java)
                            intent.putStringArrayListExtra("Followers", ArrayListFollowers)
                            context.startActivity(intent)
                        }
                    })

                    Following.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(view: View?) {
                            val intent: Intent = Intent(context, FollowingActivity::class.java)
                            intent.putExtra("Following", ArrayListFollowing)
                            context.startActivity(intent)
                        }
                    })
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

    //notifiche con FCM
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}

