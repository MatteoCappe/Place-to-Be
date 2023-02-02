package com.nomeapp.activities


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.nomeapp.fragments.ShowMyEventFragment
import com.nomeapp.models.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import java.text.SimpleDateFormat

class ShowEventActivity() : AppCompatActivity() {
    private var event: Event? = null
    private var currentUser: User? = null
    private var user: User? = null
    val context: Context = this
    var image: Uri? = null
    var userImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showevent)

        val fragmentManager = supportFragmentManager

        val searched = intent.getLongExtra("EventBoxID", 0)

        var Title: String
        var City: String
        var Address: String
        var Bio: String
        var userID: String

        val FollowUnfollow: Button =
            findViewById<View>(R.id.ShowEvent_FollowUnfollowButton) as Button
        val UserBox: CardView = findViewById<View>(R.id.ShowEvent_UserBox) as CardView
        val EditPhoto: FloatingActionButton =
            findViewById(R.id.ShowEvent_EditPhotoButton) as FloatingActionButton

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                event = getEventByID(this@ShowEventActivity, searched)
                image =
                    FirebaseStorageWrapper(this@ShowEventActivity).downloadEventImage(event!!.eventID.toString())
                currentUser = getMyData(this@ShowEventActivity)
                user = getUserByID(this@ShowEventActivity, event!!.userID)
                userImage =
                    FirebaseStorageWrapper(this@ShowEventActivity).downloadUserImage(event!!.userID)

                if (currentUser!!.Favourites!!.contains(event!!.eventID)) {
                    FollowUnfollow.text = getString(R.string.unfollow)
                    FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                }

                withContext(Dispatchers.Main) {
                    Title = event!!.Title
                    City = event!!.City
                    Address = event!!.Address
                    Bio = event!!.Bio
                    userID = event!!.userID

                    if (userID == FirebaseAuthWrapper(context).getUid()) {
                        fragmentManager.commit {
                            EditPhoto.setVisibility(View.VISIBLE)
                            setReorderingAllowed(true)
                            val frag: Fragment = ShowMyEventFragment.newInstance(event!!.eventID)
                            this.add(R.id.showMyEventFragment, frag)
                        }
                    } else {
                        FollowUnfollow.setVisibility(View.VISIBLE)
                        UserBox.setVisibility(View.VISIBLE)
                    }

                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")

                    findViewById<TextView>(R.id.ShowEvent_Title).text = Title
                    findViewById<TextView>(R.id.ShowEvent_City).text = City
                    findViewById<TextView>(R.id.ShowEvent_Address).text = Address
                    findViewById<TextView>(R.id.ShowEvent_Bio).text = Bio
                    findViewById<TextView>(R.id.ShowEvent_Date).text =
                        dateFormatter.format(event!!.Date)
                    if (image != null) {
                        findViewById<ImageView>(R.id.ShowEvent_eventImage).setImageURI(image)
                    }

                    //set user info in box
                    findViewById<TextView>(R.id.ShowEvent_UserBox_Username).text = user!!.userName
                    findViewById<TextView>(R.id.ShowEvent_UserBox_Name).text = user!!.Name
                    findViewById<TextView>(R.id.ShowEvent_UserBox_Surname).text = user!!.Surname
                    if (userImage != null) {
                        findViewById<ImageView>(R.id.ShowEvent_UserBox_Photo).setImageURI(userImage)
                    }

                    EditPhoto.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            ImagePicker.with(this@ShowEventActivity)
                                .crop()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start()
                        }
                    })

                    UserBox.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            val UsernameFromUserBox =
                                findViewById<TextView>(R.id.ShowEvent_UserBox_Username)
                            val intent: Intent = Intent(context, ShowProfileActivity::class.java)
                            intent.putExtra("UserBoxUsername", UsernameFromUserBox.text.toString())
                            startActivity(intent)
                        }
                    })

                    FollowUnfollow.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            if (FollowUnfollow.text == getString(R.string.unfollow)) {
                                currentUser!!.Favourites!!.remove(searched)
                                FirebaseDbWrapper(this@ShowEventActivity).writeDbUser(currentUser!!)

                                FollowUnfollow.text = getString(R.string.follow)
                                FollowUnfollow.setBackgroundColor(Color.parseColor("#FF6200EE"))
                            } else {
                                //in teoria non ci dovrebbe essere bisogno dei check, quindi poi vedi se toglierli
                                //per rendere il tutto più leggibile
                                //if (!currentUser!!.Favourites!!.contains(searched)) {
                                currentUser!!.Favourites!!.add(searched)
                                FirebaseDbWrapper(this@ShowEventActivity).writeDbUser(currentUser!!)

                                FollowUnfollow.text = getString(R.string.unfollow)
                                FollowUnfollow.setBackgroundColor(Color.parseColor("#808080"))
                                //}
                            }

                        }
                    })

                }
            }
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
            val searched = intent.getLongExtra("EventBoxID", 0)

            FirebaseStorageWrapper(this@ShowEventActivity).uploadEventImage(image!!, searched.toString())
            findViewById<ImageView>(R.id.ShowEvent_eventImage).setImageURI(image)
        }
    }

}