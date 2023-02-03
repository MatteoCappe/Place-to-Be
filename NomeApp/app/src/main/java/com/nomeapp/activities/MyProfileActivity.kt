package com.nomeapp.activities

//potrebbe essere unito a ShowProfileActivity con l'uso di un fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.*
import kotlinx.coroutines.*

class MyProfileActivity: AppCompatActivity() {
    //mettere casetta che rimanda alla home page (vdi fab)
    private var user: User? = null
    private var event: Event? = null
    var image: Uri? = null
    var eventList: MutableList<Event>? = arrayListOf()

    //TODO: rendere tutto profilo scrollable anzichè solo eventi

    //TODO: vedi se esiste un modo per velocizzare lettura, etichette per le varie info
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        val context: Context = this

        val userID = FirebaseAuthWrapper(context).getUid()

        val UploadImage: FloatingActionButton = findViewById<View>(R.id.myProfile_EditPhotoButton) as FloatingActionButton

        UploadImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                ImagePicker.with(this@MyProfileActivity)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start()
            }
        })

        val Followers = findViewById<View>(R.id.MyProfile_ViewFollowers) as LinearLayout
        val Following = findViewById<View>(R.id.MyProfile_ViewFollowing) as LinearLayout
        val ListOfEvents = findViewById<View>(R.id.MyProfile_EventList) as ListView

        var Username: String
        var Name: String
        var Surname: String

        //read user data
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@MyProfileActivity)
                image = FirebaseStorageWrapper(this@MyProfileActivity).downloadUserImage(userID!!)

                val ArrayListEvents: ArrayList<Long> = ArrayList(user!!.Events!!)

                if (user!!.Events!!.size != 0) {
                    for (id in ArrayListEvents) {
                        event = getEventByID(this@MyProfileActivity, id)
                        eventList!!.add(event!!)
                    }
                }

                withContext(Dispatchers.Main) {
                    Username = user!!.userName
                    Name = user!!.Name
                    Surname = user!!.Surname

                    val ArrayListFollowers: ArrayList<String> = ArrayList(user!!.Followers!!)
                    val ArrayListFollowing: ArrayList<String> = ArrayList(user!!.Following!!)

                    findViewById<TextView>(R.id.MyProfile_Username).text = Username
                    findViewById<TextView>(R.id.MyProfile_Name).text = Name
                    findViewById<TextView>(R.id.MyProfile_Surname).text = Surname
                    findViewById<TextView>(R.id.MyProfile_Followers).text = user!!.Followers!!.size.toString()
                    findViewById<TextView>(R.id.MyProfile_Following).text = user!!.Following!!.size.toString()

                    if (image != null) {
                        findViewById<ImageView>(R.id.MyProfile_ProfileImage).setImageURI(image)
                    }

                    val adapter = EventsAdapter(this@MyProfileActivity, eventList!!)
                    ListOfEvents.adapter = adapter
                    ListOfEvents.onItemClickListener =
                        AdapterView.OnItemClickListener { position, view, parent, id ->
                            val EventIDFromBox: Long =
                                view.findViewById<TextView>(R.id.EventBox_ID).text.toString().toLong()
                            val intent: Intent = Intent(context, ShowEventActivity::class.java)
                            intent.putExtra("EventBoxID", EventIDFromBox)
                            startActivity(intent)
                        }

                    Followers.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            val intent: Intent = Intent(context, FollowersActivity::class.java)
                            intent.putStringArrayListExtra("Followers", ArrayListFollowers)
                            context.startActivity(intent)
                        }
                    })

                    Following.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            val intent: Intent = Intent(context, FollowingActivity::class.java)
                            intent.putExtra("Following", ArrayListFollowing)
                            context.startActivity(intent)
                        }
                    })
                }
            }

            val LogoutButton: Button = findViewById<View>(R.id.LogoutButton) as Button
            val EditProfileButton: FloatingActionButton =
                findViewById<View>(R.id.MyProfile_EditProfileButton) as FloatingActionButton
            val AddEventButton: FloatingActionButton =
                findViewById<View>(R.id.MyProfile_AddEventButton) as FloatingActionButton

            LogoutButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val logout: Intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(logout)
                }
            })

            EditProfileButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val update: Intent = Intent(context, UpdateProfileActivity::class.java)
                    context.startActivity(update)
                }
            })

            AddEventButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val addEvent: Intent = Intent(context, AddEventActivity::class.java)
                    context.startActivity(addEvent)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                image = data?.data!!
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT).show()
            }
        }
        if (image != null) {
            FirebaseStorageWrapper(this@MyProfileActivity).uploadUserImage(image!!)
            findViewById<ImageView>(R.id.MyProfile_ProfileImage).setImageURI(image)
        }
    }

}