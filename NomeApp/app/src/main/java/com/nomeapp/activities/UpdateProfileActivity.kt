package com.nomeapp.activities

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
import com.nomeapp.models.*
import kotlinx.coroutines.*

class UpdateProfileActivity: AppCompatActivity() {
    val context: Context = this
    var image: Uri? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updateprofile)

        var alreadyused: Boolean = false

        val UploadImage: Button = findViewById<View>(R.id.UpdateProfile_UploadImage) as Button
        val SaveChanges: Button = findViewById<View>(R.id.UpdateProfile_saveChanges) as Button

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                user = getMyData(this@UpdateProfileActivity)

                withContext(Dispatchers.Main) {
                    findViewById<EditText>(R.id.Update_userName).setText(user!!.userName)
                    findViewById<EditText>(R.id.Update_Name).setText(user!!.Name)
                    findViewById<EditText>(R.id.Update_Surname).setText(user!!.Surname)

                }
            }
        }

        UploadImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                //TODO: immagine quadrata obbligatoria per profilo
                UploadImage.setOnClickListener {
                    ImagePicker.with(this@UpdateProfileActivity)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start()
                }
            }
        })

        SaveChanges.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val Name: EditText = findViewById<View>(R.id.Update_Name) as EditText
                val Surname: EditText = findViewById<View>(R.id.Update_Surname) as EditText
                val userName: EditText = findViewById<View>(R.id.Update_userName) as EditText

                if (Name.text.isEmpty() || Surname.text.isEmpty() || userName.text.isEmpty()) {
                    Name.setError(getString(R.string.emptyError))
                    Surname.setError(getString(R.string.emptyError))
                    userName.setError(getString(R.string.emptyError))
                    return
                }

                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            user = getMyData(this@UpdateProfileActivity)
                            alreadyused = usernameAlreadyExists(view!!.context, userName.text.toString())

                            withContext(Dispatchers.Main) {
                                if (alreadyused) {
                                    userName.setError(getString(R.string.usernameError))
                                }
                                else {
                                    user!!.userName = userName.text.toString()
                                    user!!.Name = Name.text.toString()
                                    user!!.Surname = Surname.text.toString()

                                    FirebaseDbWrapper(this@UpdateProfileActivity).writeDbUser(user!!)
                                    val returnToProfile: Intent = Intent(context, MyProfileActivity::class.java)
                                    context.startActivity(returnToProfile)
                                }
                            }
                        }
                    }
                }
            }
        })
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
        if(image != null) {
            FirebaseStorageWrapper(this@UpdateProfileActivity).uploadUserImage(image!!)
        }
    }

}