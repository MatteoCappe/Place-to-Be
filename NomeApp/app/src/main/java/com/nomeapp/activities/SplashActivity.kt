package com.nomeapp.activities

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
    private var allPermissionGranted : Boolean? = null

    fun hasPermission() : Boolean {
        if (allPermissionGranted == null) {
            for (permission in GrantPermissionListener.PERMISSION_NEEDED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }

            return true
        }

        return allPermissionGranted!!
    }

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

        // Start Main Activity
        if (this.hasPermission()) {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
            return
        }

        // Show a message and react to a click button
        // grantPermission
        val grantPermissionButton : Button = this.findViewById<Button>(R.id.grantPermission);
        val listener : View.OnClickListener = GrantPermissionListener(this);
        grantPermissionButton.setOnClickListener(listener);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d(TAG, "onRequestPermissionsResult")

        // Check if the permission are granted or

        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "A needed permission is not granted!");
                allPermissionGranted = false
                return
            }
        }

        this.allPermissionGranted = true
        val intent = Intent(this, SplashActivity::class.java)
        this.startActivity(intent)
        finish()
    }
}