package com.nomeapp.activities

//TODO: fix splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.nomeapp.R
import com.nomeapp.listeners.GrantPermissionListener
import com.nomeapp.models.FirebaseAuthWrapper

class SplashActivity : AppCompatActivity() {
    private val TAG = SplashActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Log.d(TAG, "The SplashActivity is started!")

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
            // Start Main Activity
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
            return
        }
    }
}