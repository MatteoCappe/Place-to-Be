package com.nomeapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.fragments.SearchUserFragment
//import com.nomeapp.fragments.UnfollowFragment
//TODO: fix problema su fragment
import com.nomeapp.models.*
import kotlinx.coroutines.*

class ShowProfileActivity(): AppCompatActivity() {
    private var user: User? = null
    private var currentUser: User? = null
    val context: Context = this
    var image: Uri? = null
    val fragmentManager = supportFragmentManager

    //TODO: vedi se esiste un modo per velocizzare lettura, etichette per le varie informazioni
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showprofile)

        val userID: String? = FirebaseAuthWrapper(this@ShowProfileActivity).getUid()
        val searched = intent.getStringExtra("UserBoxUsername")!!

        //val searched: String = "Prova" //questo dovrà essere inizializzato con una stringa
                                       //derivante dalla funzione "ricerca"
                                       //momentaneamente è inizializzato a "Prova" per far vedere come funzionerebbe

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getUserByUsername(this@ShowProfileActivity, searched)
                currentUser = getMyData(this@ShowProfileActivity)

                //TODO: check che funzioni davvero quando si fa lista follower
                if (user!!.UserID == userID!!) {
                    val intent: Intent = Intent(context, MyProfileActivity::class.java)
                    startActivity(intent)
                }
                //potrebbe servire se si mettono i follower
                //per evitare che si rompa ogni volta che ci si clicca sopra

                image = FirebaseStorageWrapper(this@ShowProfileActivity).downloadUserImage(user!!.UserID)

                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.ShowProfile_Username).text = user!!.userName
                    findViewById<TextView>(R.id.ShowProfile_Name).text = user!!.Name
                    findViewById<TextView>(R.id.ShowProfile_Surname).text = user!!.Surname
                    if (image != null) {
                        findViewById<ImageView>(R.id.ShowProfile_profileImage).setImageURI(image)
                    }
                }
            }
        }

        //chek se funziona per davvero

        //follow user
        val Follow: Button = findViewById<View>(R.id.ShowProfile_Follow) as Button

        Follow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                currentUser!!.Following!!.add(user!!.UserID)
                user!!.Followers!!.add(userID!!)

                /*fragmentManager.commit {
                    setReorderingAllowed(true)
                    val frag: Fragment = UnfollowFragment.newInstance(user!!.UserID)
                    this.replace(R.id.SearchUserFragment, frag)
                }*/
                //TODO: fragment bottone grigio, copia da Ruggia
            }
        })

        //unfollow?

    }

}