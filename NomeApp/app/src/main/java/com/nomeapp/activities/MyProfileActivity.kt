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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nomeapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.models.FirebaseStorageWrapper
import com.nomeapp.models.User
import com.nomeapp.models.getMyData
import com.nomeapp.models.usernameAlreadyExists //da aggiungere in UpdateProfileActivity
import kotlinx.coroutines.*

class MyProfileActivity: AppCompatActivity() {
    //mettere freccettina che rimanda alla home page
    private var user: User? = null
    val context: Context = this //vedi se serve
    var profileImage: ImageView? = null
    var image: Uri? = null

    //TODO: vedi se esiste un modo per velocizzare lettura
    //TODO: colore e posizione info
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        val MyProfileUploadImage: Button = findViewById<View>(R.id.MyProfile_UploadImage) as Button
        val profileImage: ImageView = findViewById<View>(R.id.MyProfile_profileImage) as ImageView

        //se profileImage == null, metti omino vuoto

        MyProfileUploadImage.setOnClickListener {
            ImagePicker.with(this@MyProfileActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        var Username: String
        var Name: String
        var Surname: String
        var userID: String

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@MyProfileActivity)
                withContext(Dispatchers.Main) {
                    //TODO: immagine nello storage
                    Username = user!!.userName
                    Name = user!!.Name
                    Surname = user!!.Surname
                    userID = user!!.UserID

                    findViewById<TextView>(R.id.MyProfile_Username).text = Username
                    findViewById<TextView>(R.id.MyProfile_Name).text = Name
                    findViewById<TextView>(R.id.MyProfile_Surname).text = Surname
                }
            }
        }

        if(image != null) {
            //delete, poi vedi con update activity
            FirebaseStorageWrapper(this@MyProfileActivity).uploadUserImage(image!!)
            findViewById<ImageView>(R.id.MyProfile_profileImage).setImageURI(image) //check se funziona
        }
        //else -> omino vuoto

        val EditProfileButton: FloatingActionButton = findViewById<View>(R.id.MyProfile_EditProfileButton) as FloatingActionButton
        val AddEventButton: FloatingActionButton = findViewById<View>(R.id.MyProfile_AddEventButton) as FloatingActionButton

        /*EditProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //vedi se serve userID
                val intent: Intent = Intent(context, UpdateProfileActivity::class.java)
                context.startActivity(intent)
            }
        })*/

        AddEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //vedi se serve userID
                val intent: Intent = Intent(context, AddEventActivity::class.java)
                context.startActivity(intent)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                image = data?.data!!
                profileImage!!.setImageURI(image)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}