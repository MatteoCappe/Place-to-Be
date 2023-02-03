package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.*
import kotlinx.coroutines.*

class ShowProfileActivity(): AppCompatActivity() {
    private var user: User? = null
    private var currentUser: User? = null
    private var event: Event? = null
    var image: Uri? = null
    var eventList: MutableList<Event>? = arrayListOf()
    //var followersList: MutableList<String> = arrayListOf()

    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showprofile)

        val FollowUnfollow: Button = findViewById<View>(R.id.ShowProfile_FollowUnfollowButton) as Button
        val Followers = findViewById<View>(R.id.ShowProfile_ViewFollowers) as LinearLayout
        val Following = findViewById<View>(R.id.ShowProfile_ViewFollowing) as LinearLayout
        val ListOfEvents = findViewById<View>(R.id.ShowProfile_EventList) as ListView

        val userID: String? = FirebaseAuthWrapper(this@ShowProfileActivity).getUid()
        val searched = intent.getStringExtra("UserBoxUsername")!!

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getUserByUsername(this@ShowProfileActivity, searched)
                currentUser = getMyData(this@ShowProfileActivity)
                image =
                    FirebaseStorageWrapper(this@ShowProfileActivity).downloadUserImage(user!!.UserID)
                //followersList = getFollowers(this@ShowProfileActivity, user!!.UserID)
                //TODO forse togliendo il commento a qua sopra fa effettivamente il cambio che voglio

                val ArrayListEvents: ArrayList<Long> = ArrayList(user!!.Events!!)
                val ArrayListFollowers: ArrayList<String> = ArrayList(user!!.Followers!!)
                val ArrayListFollowing: ArrayList<String> = ArrayList(user!!.Following!!)

                if (user!!.Events!!.size != 0) {
                    for (id in ArrayListEvents) {
                        event = getEventByID(this@ShowProfileActivity, id)
                        eventList!!.add(event!!)
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

                                //update numerino
                                findViewById<TextView>(R.id.ShowProfile_Followers).text = (user!!.Followers!!.size).toString()

                                ArrayListFollowers.remove(userID!!)

                                FollowUnfollow.text = getString(R.string.follow)
                                FollowUnfollow.setBackgroundColor(Color.parseColor("#FF6200EE"))
                            }
                            else {
                                //in teoria non ci dovrebbe essere bisogno dei check, quindi poi vedi se toglierli
                                //per rendere il tutto più leggibile
                                if (!currentUser!!.Following!!.contains(user!!.UserID)) {
                                    currentUser!!.Following!!.add(user!!.UserID)
                                    FirebaseDbWrapper(this@ShowProfileActivity).writeDbUser(currentUser!!)

                                    FollowUnfollow.text = getString(R.string.unfollow)
                                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                                }

                                if (!user!!.Followers!!.contains(userID!!)) {
                                    user!!.Followers!!.add(userID!!)
                                    FirebaseDbWrapper(this@ShowProfileActivity).writeDbShownUser(user!!)
                                    Log.d("gianni", "1")
                                    //FirebaseDbWrapper(this@ShowProfileActivity).writeDbFollower(user!!.UserID, user!!.Followers!!)

                                    //update numerino
                                    findViewById<TextView>(R.id.ShowProfile_Followers).text = (user!!.Followers!!.size).toString()

                                    ArrayListFollowers.add(userID!!)

                                    FollowUnfollow.text = getString(R.string.unfollow)
                                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                                    //tecnicamente inutile ripeterlo due volte ma vbb
                                }
                                /*else {
                                    Log.d("gianni", "porca troia 1")
                                    followersList.add(userID!!)
                                    FirebaseDbWrapper(this@ShowProfileActivity).writeDbFollower(user!!.UserID, user!!.Followers!!)
                                }*/
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

}