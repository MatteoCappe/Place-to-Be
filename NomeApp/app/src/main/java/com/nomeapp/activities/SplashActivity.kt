package com.nomeapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.nomeapp.R
import com.nomeapp.models.FirebaseAuthWrapper

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Check if user logged or not
        val firebaseAuthWrapper : FirebaseAuthWrapper = FirebaseAuthWrapper(this)
        if (!firebaseAuthWrapper.isAuthenticated()) {
            // Redirect to login/register activity
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
            return
        }
        else {
            //Start Main Activity
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
            return
        }
    }
}