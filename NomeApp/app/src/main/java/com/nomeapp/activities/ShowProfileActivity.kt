package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
//import com.nomeapp.fragments.FollowUnfollowFragment
//TODO: fix problema su fragment
import com.nomeapp.models.*
import kotlinx.coroutines.*

class ShowProfileActivity(): AppCompatActivity() {
    private var user: User? = null
    private var currentUser: User? = null
    val context: Context = this
    var image: Uri? = null

    //TODO: vedi se esiste un modo per velocizzare lettura, etichette per le varie informazioni
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showprofile)

        val userID: String? = FirebaseAuthWrapper(this@ShowProfileActivity).getUid()
        val searched = intent.getStringExtra("UserBoxUsername")!!

        var FollowUnfollow: Button = findViewById<View>(R.id.ShowProfile_FollowUnfollowButton) as Button

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getUserByUsername(this@ShowProfileActivity, searched)
                currentUser = getMyData(this@ShowProfileActivity)
                image =
                    FirebaseStorageWrapper(this@ShowProfileActivity).downloadUserImage(user!!.UserID)

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
                    if (image != null) {
                        findViewById<ImageView>(R.id.ShowProfile_profileImage).setImageURI(image)
                    }

                    FollowUnfollow.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            if (FollowUnfollow.text == getString(R.string.unfollow)) {
                                currentUser!!.Following!!.remove(user!!.UserID)
                                FirebaseDbWrapper(this@ShowProfileActivity).writeDbUser(currentUser!!)

                                user!!.Followers!!.remove(userID!!)
                                FirebaseDbWrapper(this@ShowProfileActivity).writeDbShownUser(user!!)

                                FollowUnfollow.text = getString(R.string.follow)
                                FollowUnfollow.setBackgroundColor(Color.parseColor("#FF6200EE")) //vedi
                            }
                            else {
                                //in teoria non ci dovrebbe essere bisogno dei check, quindi poi vedi se toglierli
                                //per rendere il tutto più leggibile
                                if (!currentUser!!.Following!!.contains(user!!.UserID)) {
                                    currentUser!!.Following!!.add(user!!.UserID)
                                    FirebaseDbWrapper(this@ShowProfileActivity).writeDbUser(currentUser!!)

                                    FollowUnfollow.text = getString(R.string.unfollow)
                                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080")) //vedi
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