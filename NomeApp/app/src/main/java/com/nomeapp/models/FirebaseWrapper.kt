package com.nomeapp.models


import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.nomeapp.activities.SplashActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashMap
import kotlin.concurrent.withLock

// NOTE: With firebase we have to do a network request --> We need to add the permission in the AndroidManifest.xml
//      -> ref: https://developer.android.com/training/basics/network-ops/connecting
// Firebase auth - https://firebase.google.com/docs/auth/android/start#kotlin+ktx
// Firebase db - https://firebase.google.com/docs/database/android/start?hl=en
// 1) Create a new project from https://firebase.google.com/
// 2) Start with the authenication
// 2) Create a new database in the project
//      --> Types of databases: https://firebase.google.com/docs/database/rtdb-vs-firestore?hl=en
// 3) In Android Studio: Tools > Firebase > Realtime Database > Save and retrieve data
// 4) Follow the steps!

// Stupid issue: https://stackoverflow.com/questions/56266801/java-net-socketexception-socket-failed-eperm-operation-not-permitted
//  -> I only have to uninstall the app from the emulator and then it works!


class FirebaseAuthWrapper(private val context: Context) {
    private val TAG : String = FirebaseAuthWrapper::class.simpleName.toString()
    private var auth: FirebaseAuth = Firebase.auth

    fun isAuthenticated() : Boolean {
        return auth.currentUser != null
    }

    fun getUid() : String? {
        return auth.currentUser?.uid
    }

    fun signUp(email: String, password: String) {
        this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success -> ask for permission
                Log.d(TAG, "createUserWithEmail:success")
                logSuccess()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, "Sign-up failed. Error message: ${task.exception!!.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    logSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun logSuccess() {
        val intent : Intent = Intent(this.context, SplashActivity::class.java)
        context.startActivity(intent)
    }

}
/*
fun mergeMyEventWithFirebaseInfo (context: Context, events: List<MyEvent>) {
    val lock = ReentrantLock()
    val condition = lock.newCondition()

    val mapFirebaseEvents = HashMap<Long, FirebaseDbWrapper.Companion.FirebaseEvent>()
    GlobalScope.launch{
        FirebaseDbWrapper(context).readDbData(object : FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")

                for (child in snapshot.children){
                    val firebaseEvent : FirebaseDbWrapper.Companion.FirebaseEvent = child.getValue(FirebaseDbWrapper.Companion.FirebaseEvent::class.java)!!
                    mapFirebaseEvents.put(firebaseEvent.id, firebaseEvent)
                }

                lock.withLock {
                    condition.signal()
                }
            }

            override fun onCancelledCallback(error: DatabaseError) {
                Log.d("onCancelledCallback", "invoked")
            }
        })
    }

    // TODO: merge
    lock.withLock {
        condition.await()
    }
/*
    for (myEvent : MyEvent in events) {
        myEvent.isWeather = mapFirebaseEvents[myEvent.id]?.weather ?: myEvent.isWeather
        myEvent.location = mapFirebaseEvents[myEvent.id]?.location ?: myEvent.location
    }
*/
}
*/
// Database
/*
// NB: For security reason update the access rules from firebase console --> an user can access on its data!
// rules: https://firebase.google.com/docs/rules/rules-and-auth?authuser=0
// doc: https://firebase.google.com/docs/rules/basics?utm_source=studio#realtime-database_5
// Example
{
  "rules": {
    "events": {
      "$uid": {
        ".read": "auth.uid === $uid",
        ".write": "auth.uid === $uid",
      }
    }
  },
}
 */
class FirebaseDbWrapper(private val context: Context) {
    private val TAG: String = FirebaseDbWrapper::class.simpleName.toString()
    private val CHILD: String = "events"

    private fun getDb() : DatabaseReference? {
        val ref = Firebase.database.getReference(CHILD)

        val uid = FirebaseAuthWrapper(context).getUid()
        if (uid == null) {
            return null;
        }

        return ref.child(uid)
    }

    fun writeDbData(firebaseEvent: FirebaseEvent) {
        val ref = getDb()

        if (ref == null) {
            return;
        }

        ref.child(firebaseEvent.id.toString()).setValue(firebaseEvent)
    }

    fun readDbData(callback : FirebaseReadCallback) {
        val ref = getDb()

        if (ref == null) {
            return;
        }

        // Read from the database
        ref.addValueEventListener(FirebaseReadListener(callback))
    }

    companion object {
        class FirebaseEvent() {
            var id : Long = -1
            var weather: Boolean = false
            var location: String = ""

            constructor(id: Long, weather: Boolean, location: String) : this() {
                this.id = id
                this.location = location
                this.weather = weather
            }
        }

        class FirebaseReadListener(val callback : FirebaseReadCallback) : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback.onDataChangeCallback(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onCancelledCallback(error)
            }
        }

        interface FirebaseReadCallback {
            fun onDataChangeCallback(snapshot: DataSnapshot);
            fun onCancelledCallback(error: DatabaseError);
        }
    }

}