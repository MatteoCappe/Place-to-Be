package com.nomeapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.nomeapp.R



// Form validation example: https://www.geeksforgeeks.org/implement-form-validation-error-to-edittext-in-android/

// pm grant <package-name> <permission>

// Services: https://developer.android.com/guide/components/services
// WorkerManager: https://developer.android.com/topic/libraries/architecture/workmanager/basics
// -) Periodic: https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work#schedule_periodic_work
// -) OneTime: https://developer.android.com/reference/kotlin/androidx/work/OneTimeWorkRequest

// Example code: our secret is not secure - Hardcoded!
//        /*
//        val SECRET_KEY: String = getString(R.string.secret_key)
//
//        var str = "]HYVM"
//        val res = str.toCharArray()
//        for (i in 0..(SECRET_KEY.length-1)) {
//            res[i] = res[i].code.xor(SECRET_KEY[i].code).toChar();
//        }
//        str = String(res)
//
//        Log.d("MainActivity", "Decrypted key = ${str}")
//        */
class MainActivity : AppCompatActivity() {
    private val TAG : String? = MainActivity::class.simpleName
    val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val MyProfileButton: Button = findViewById<View>(R.id.MyProfileButton) as Button

        MyProfileButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, MyProfileActivity::class.java)
                context.startActivity(intent)
            }
        })

        //TODO: link alle varie pagine

        // Create Fragment on https://developer.android.com/reference/android/widget/CalendarView#setOnDateChangeListener(android.widget.CalendarView.OnDateChangeListener)
        //val fragmentManager = this.getSupportFragmentManager()
        //val context : Context = this


    }


}