
package com.nomeapp.activities



import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
//import androidx.activity.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.nomeapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.nomeapp.models.*
import kotlinx.coroutines.*


class RegisterActivity : AppCompatActivity() {
    val context: Context = this
    private lateinit var firebaseAuth: FirebaseAuth
    var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val UploadImage: Button = findViewById<View>(R.id.UploadImage) as Button
        val switch: TextView = findViewById<View>(R.id.switchToLogin) as TextView
        val LoginButton: Button = findViewById<View>(R.id.registerButton) as Button

        var alreadyused: Boolean = false

        UploadImage.setOnClickListener {
            ImagePicker.with(this@RegisterActivity)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        switch.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        LoginButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val Name: EditText = findViewById<View>(R.id.Name) as EditText
                val Surname: EditText = findViewById<View>(R.id.Surname) as EditText
                val userName: EditText = findViewById<View>(R.id.userName) as EditText
                val email: EditText = findViewById<View>(R.id.userEmail) as EditText
                val password: EditText = findViewById<View>(R.id.userPassword) as EditText

                if(image != null) {
                    //non serve delete in quanto è la prima messa
                    FirebaseStorageWrapper(this@RegisterActivity).uploadUserImage(image!!)
                }

                if (Name.text.isEmpty() || Surname.text.isEmpty() || userName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty()) {
                    Name.setError("This is required")
                    Surname.setError("This is required")
                    userName.setError("This is required")
                    email.setError("This is required")
                    password.setError("This is required")
                    return
                }

                else {
                    CoroutineScope(Dispatchers.Main + Job()).launch {
                        withContext(Dispatchers.IO) {
                            alreadyused = usernameAlreadyExists(view!!.context, userName.text.toString())
                            withContext(Dispatchers.Main) {
                                if (alreadyused) {
                                    userName.setError("This username is already in use")
                                }
                                else {
                                    val user = User(
                                        userName.text.toString(),
                                        Name.text.toString(),
                                        Surname.text.toString(),
                                        "null"
                                    )

                                    action(user, email.text.toString(), password.text.toString())
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun action(user: User, email: String, password: String) {
        val firebaseAuthWrapper: FirebaseAuthWrapper = FirebaseAuthWrapper(context)
        firebaseAuthWrapper.signUp(user, email, password)
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
    }
}