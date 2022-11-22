package com.nomeapp.listeners


import android.Manifest
import android.app.Activity
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat

class GrantPermissionListener(val activity : Activity) : View.OnClickListener {
    private val TAG : String? = GrantPermissionListener::class.simpleName
    private val PERMISSION_REQUEST_ID = 123;

    override fun onClick(v: View?) {
        Log.d(TAG, "grantPermission button clicked")

        // Request permissions
        ActivityCompat.requestPermissions(this.activity,
            PERMISSION_NEEDED,
            PERMISSION_REQUEST_ID)

    }

    companion object {
        public val PERMISSION_NEEDED = arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
    }

}