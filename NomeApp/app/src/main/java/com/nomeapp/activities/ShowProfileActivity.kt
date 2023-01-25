package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.*
import kotlinx.coroutines.*

class ShowProfileActivity(): AppCompatActivity() {
    private var user: User? = null
    private var currentUser: User? = null
    private var event: Event? = null
    var image: Uri? = null
    var eventList: MutableList<Event>? = arrayListOf()

    val context: Context = this

    //Show

    //TODO: vedi se esiste un modo per velocizzare lettura, etichette per le varie informazioni
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

                val ArrayListEvents: ArrayList<Long> = ArrayList(user!!.Events!!)

                if (user!!.Events!!.size != 0) {
                    for (id in ArrayListEvents) {
                        event = getEventByID(this@ShowProfileActivity, id)
                        eventList!!.add(event!!)
                    }
                }

                //TODO: check che funzioni davvero quando si fa lista follower
                if (user!!.UserID == userID!!) {
                    val intent: Intent = Intent(context, MyProfileActivity::class.java)
                    startActivity(intent)
                }

                if (currentUser!!.Following!!.contains(user!!.UserID)) {
                    FollowUnfollow.text = getString(R.string.unfollow)
                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080")) //vedi
                }

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.ShowProfile_Username).text = user!!.userName
                    findViewById<TextView>(R.id.ShowProfile_Name).text = user!!.Name
                    findViewById<TextView>(R.id.ShowProfile_Surname).text = user!!.Surname
                    findViewById<TextView>(R.id.ShowProfile_Followers).text = user!!.Followers!!.size.toString()
                    findViewById<TextView>(R.id.ShowProfile_Following).text = user!!.Following!!.size.toString()

                    val ArrayListFollowers: ArrayList<String> = ArrayList(user!!.Followers!!)
                    val ArrayListFollowing: ArrayList<String> = ArrayList(user!!.Following!!)

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

                    Followers.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(view: View?) {
                            if (user!!.Followers!!.size == 0) {
                                val intent: Intent = Intent(context, FollowersNotFoundActivity::class.java)
                                context.startActivity(intent)
                            }
                            else {
                                val intent: Intent = Intent(context, FollowersActivity::class.java)
                                intent.putStringArrayListExtra("Followers", ArrayListFollowers)
                                context.startActivity(intent)
                            }
                        }
                    })

                    Following.setOnClickListener(object: View.OnClickListener {
                        override fun onClick(view: View?) {
                            if (user!!.Following!!.size == 0) {
                                val intent: Intent = Intent(context, FollowingNotFoundActivity::class.java)
                                context.startActivity(intent)
                            }
                            else {
                                val intent: Intent = Intent(context, FollowingActivity::class.java)
                                intent.putExtra("Following", ArrayListFollowing)
                                context.startActivity(intent)
                            }
                        }
                    })

                    //TODO: check perche se cerca -> apri profilo -> follow/unfollow
                    //TODO: poi da problemi appena o riapro profilo o provo ad  aprirne un altro
                    //TODO: guarda se si risolve con fragment

                    FollowUnfollow.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            if (FollowUnfollow.text == getString(R.string.unfollow)) {
                                currentUser!!.Following!!.remove(user!!.UserID)
                                FirebaseDbWrapper(this@ShowProfileActivity).writeDbUser(currentUser!!)

                                user!!.Followers!!.remove(userID!!)
                                FirebaseDbWrapper(this@ShowProfileActivity).writeDbShownUser(user!!)

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

                                    FollowUnfollow.text = getString(R.string.unfollow)
                                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080")) //vedi
                                    //tecnicamente inutile ripeterlo due volte ma vbb
                                }
                            }

                        }
                    })
                }
            }
        }
    }

}