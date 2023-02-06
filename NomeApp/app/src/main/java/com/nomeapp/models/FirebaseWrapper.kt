package com.nomeapp.models


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.nomeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import com.nomeapp.activities.MainActivity
import com.nomeapp.activities.SplashActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseAuthWrapper(private val context: Context) {
    private val TAG : String = FirebaseAuthWrapper::class.simpleName.toString()
    private var auth: FirebaseAuth = Firebase.auth

    fun isAuthenticated() : Boolean {
        return auth.currentUser != null
    }

    fun getUid() : String? {
        return auth.currentUser?.uid
    }

    fun getEmail() : String? {
        return auth.currentUser?.email
    }

    fun signUp(user: User, email: String, password: String) {
        this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.UserID = FirebaseAuthWrapper(context).getUid().toString()
                FirebaseDbWrapper(context).writeDbUser(user)
                logSuccess()
            } else {
                // If sign up fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    context,
                    "Sign-up failed. Error message: ${task.exception!!.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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

class FirebaseDbWrapper(private val context: Context) {
    private var database = Firebase.database("https://nomeapp-fa2db-default-rtdb.europe-west1.firebasedatabase.app/")
    val ref = database.reference
    private val userID = FirebaseAuthWrapper(context).getUid()

    fun writeDbUser(user: User) {
        ref.child("users").child(userID!!).setValue(user)
    }

    fun writeDbShownUser(user: User) {
        ref.child("users").child(user.UserID).setValue(user)
    }

    fun writeDbEvent(event: Event, eventID: Long) {
        ref.child("events").child(eventID.toString()).setValue(event)
    }

    /*fun writeDbFollower(uid: String, followers: MutableList<String>) {
        ref.child("followers").child(uid).setValue(followers)
    } //TODO: fix*/

    fun readDbData(callback: FirebaseReadCallback) {
        ref.addValueEventListener(FirebaseReadListener(callback))
    }

    companion object {
        class FirebaseReadListener(val callback: FirebaseReadCallback) : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback.onDataChangeCallback(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onCancelledCallback(error)
            }
        }
        interface FirebaseReadCallback {
            fun onDataChangeCallback(snapshot: DataSnapshot) //success method
            fun onCancelledCallback(error: DatabaseError) //error method
        }
    }

}

class FirebaseStorageWrapper (private val context: Context) {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    val userID = FirebaseAuthWrapper(context).getUid().toString()

    fun uploadUserImage (userImage: Uri) {
        storageRef.child("users/${userID}.jpg").putFile(userImage)
    }

    fun uploadEventImage (eventImage: Uri, eventID: String) {
        storageRef.child("events/${eventID}.jpg").putFile(eventImage)
    }

    fun downloadUserImage (userID: String): Uri? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var image: Uri? = null
        val localFile = File.createTempFile("users", "jpg")

        GlobalScope.launch {
            storageRef.child("users/${userID}.jpg").getFile(localFile).addOnSuccessListener {
                image = Uri.fromFile(localFile)

                lock.withLock {
                    condition.signal()
                }
            }.addOnFailureListener {

                lock.withLock {
                    condition.signal()
                }
            }
        }

        lock.withLock {
            condition.await()
        }
        return image
    }

    fun downloadEventImage (eventID: String): Uri? {
        val lock = ReentrantLock()
        val condition = lock.newCondition()
        var image: Uri? = null
        val localFile = File.createTempFile("events", "jpg")

        GlobalScope.launch {
            storageRef.child("events/${eventID}.jpg").getFile(localFile).addOnSuccessListener {
                image = Uri.fromFile(localFile)

                lock.withLock {
                    condition.signal()
                }
            }.addOnFailureListener {

                lock.withLock {
                    condition.signal()
                }
            }
        }

        lock.withLock {
            condition.await()
        }
        return image
    }

}

class FirebaseMessagingWrapper : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "Follower"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}

fun usernameAlreadyExists(context: Context, userName: String): Boolean {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var alreadyexists: Boolean = false

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (users in snapshot.child("users").children) {
                    if (users.child("userName").getValue(String::class.java)!!.equals(userName)) {
                        alreadyexists = true
                        break
                    }
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
    lock.withLock {
        condition.await()
    }
    return alreadyexists
}

fun getMyData(context: Context): User {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var user: User? = null
    val userID = FirebaseAuthWrapper(context).getUid()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object:
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                user = snapshot.child("users").child(userID!!).getValue(User::class.java)

                lock.withLock {
                    condition.signal()
                }
            }

            override fun onCancelledCallback(error: DatabaseError) {
                Log.d("onCancelledCallback", "invoked")
            }
        })
    }
    lock.withLock {
        condition.await()
    }
    return user!!
}

fun getUserByUsername(context: Context, userName: String): User {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var user: User? = null

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (users in snapshot.child("users").children) {
                    if (users.child("userName").getValue(String::class.java)!!.equals(userName)) {
                        val userID: String = users.child("userID").getValue(String::class.java).toString()
                        user = snapshot.child("users").child(userID).getValue(User::class.java)
                        break
                    }
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
    lock.withLock {
        condition.await()
    }
    return user!!
}

fun getUserByID(context: Context, userID: String): User {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var user: User? = null

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (users in snapshot.child("users").children) {
                    if (users.child("userID").getValue(String::class.java)!!.equals(userID)) {
                        user = snapshot.child("users").child(userID).getValue(User::class.java)
                        break
                    }
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
    lock.withLock {
        condition.await()
    }
    return user!!
}

fun getEventID(context: Context): Long {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var eventID: Long = 0

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                val events = snapshot.child("events").children
                for (id in events) {
                    if (id.key!!.toLong() > eventID) {
                        eventID = id.key!!.toLong()
                    }
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
    lock.withLock {
        condition.await()
    }
    eventID++
    return eventID!!
}

fun getEventByID(context: Context, EventID: Long): Event {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var event: Event? = null

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (events in snapshot.child("events").children) {
                    if (events.child("eventID").getValue(Long::class.java)!!.equals(EventID)) {
                        event = snapshot.child("events").child(EventID.toString()).getValue(Event::class.java)
                        break
                    }
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
    lock.withLock {
        condition.await()
    }
    return event!!
}

fun getUsersByUsernameStart (context: Context, userName: String): MutableList<User> {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var userList: MutableList<User> = ArrayList()
    val uid = FirebaseAuthWrapper(context).getUid()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (users in snapshot.child("users").children) {
                    val user = users.getValue(User::class.java)
                    if (user!!.userName.startsWith(userName, true) && user!!.UserID != uid) {
                        userList.add(user!!)
                    }
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
    lock.withLock {
        condition.await()
    }
    return userList

}

fun SearchEvent (context: Context, Title: String, City: String, Date: String): MutableList<Event> {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var eventList: MutableList<Event> = ArrayList()

    val currentTime = Calendar.getInstance()

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (events in snapshot.child("events").children) {
                    val event = events.getValue(Event::class.java)

                    if (Title.length > 0 && City.length == 0 && Date.length == 0) {
                        if (event!!.Title.startsWith(Title, true) &&
                            LocalDateTime.parse(event!!.formattedDate, formatter).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() > currentTime.timeInMillis) { //mostra eventi successivi alla data odierna
                            eventList.add(event!!)
                        }
                    }

                    else if (City.length > 0 && Title.length == 0 && Date.length == 0) {
                        if (event!!.City.lowercase().equals(City.lowercase()) &&
                            LocalDateTime.parse(event!!.formattedDate, formatter).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() > currentTime.timeInMillis) {
                            eventList.add(event!!)
                        }
                    }

                    else if (Date.length > 0 && Title.length == 0 && City.length == 0) {
                        val DBDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        val convertedDate = DBDate.parse(event!!.formattedDate!!)
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(convertedDate!!)
                        if (Date.equals(formattedDate) ) {
                            eventList.add(event!!)
                        }
                    }

                    else if(Title.length > 0 && City.length > 0 && Date.length == 0) {
                        if (event!!.Title.startsWith(Title, true) &&
                            event!!.City.lowercase().equals(City.lowercase()) &&
                            LocalDateTime.parse(event!!.formattedDate, formatter).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() > currentTime.timeInMillis) {
                            eventList.add(event!!)
                        }
                    }

                    else if(Title.length > 0 && Date.length > 0 && City.length == 0) {
                        val DBDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        val convertedDate = DBDate.parse(event!!.formattedDate!!)
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(convertedDate!!)
                        if (event!!.Title.startsWith(Title, true) && Date.equals(formattedDate)) {
                            eventList.add(event!!)
                        }
                    }

                    else if(City.length > 0 && Date.length > 0 && Title.length == 0) {
                        val DBDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        val convertedDate = DBDate.parse(event!!.formattedDate!!)
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(convertedDate!!)
                        if (event!!.City.lowercase().equals(City.lowercase()) && Date.equals(formattedDate)) {
                            eventList.add(event!!)
                        }
                    }

                    else {
                        val DBDate = SimpleDateFormat("yyyy-MM-dd HH:mm")
                        val convertedDate = DBDate.parse(event!!.formattedDate!!)
                        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(convertedDate!!)
                        if (event!!.Title.startsWith(Title, true) &&
                            event!!.City.lowercase().equals(City.lowercase()) && Date.equals(formattedDate)) {
                            eventList.add(event!!)
                        }
                    }
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
    lock.withLock {
        condition.await()
    }
    return eventList

}

fun DeleteEvent (context: Context, EventID: Long) {
    val lock = ReentrantLock()
    val condition = lock.newCondition()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                snapshot.child("events").child(EventID.toString()).ref.removeValue()
                FirebaseStorage.getInstance().reference.child("events/${EventID}.jpg").delete()

                Log.d("onDataChangeCallback", "invoked")
                for (users in snapshot.child("users").children) {
                    val user = users.getValue(User::class.java)
                    if (user!!.Events!!.contains(EventID)) {
                        user!!.Events!!.remove(EventID)
                        FirebaseDbWrapper(context).writeDbUser(user)
                    }
                    if (user!!.Favourites!!.contains(EventID)) {
                        user!!.Favourites!!.remove(EventID)
                        FirebaseDbWrapper(context).writeDbShownUser(user)
                    }
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
    lock.withLock {
        condition.await()
    }
}

/*fun SendFollow (context: Context, followerUID: String) {
    Log.d("gianni", "5")
    val userID = FirebaseAuthWrapper(context).getUid()
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var followersUID: MutableList<String> = ArrayList()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                Log.d("gianni", "6")
                //followersUID = snapshot.child("followers").child(userID!!).value as MutableList<String>
                followersUID.add(followerUID)

                lock.withLock {
                    condition.signal()
                }
            }

            override fun onCancelledCallback(error: DatabaseError) {
                Log.d("onCancelledCallback", "invoked")
            }
        })
    }
    lock.withLock {
        condition.await()
    }

    Log.d("gianni", "7")
    FirebaseDbWrapper(context).ref.child("followers").child(userID!!).setValue(followersUID)
    Log.d("gianni", "8")
}*/

/*
//TODO: fix
fun getFollowers(context: Context): MutableList<String> {
    val uid = FirebaseAuthWrapper(context).getUid()
    Log.d("gianni", "9")

    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var list: MutableList<String> = ArrayList()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")



                //TODO
                for (follower in snapshot.child("followers").child(uid!!).children) {
                    Log.d("gianni", "10")
                    list.add(follower.getValue(String::class.java)!!)
                }
                Log.d("gianni", "11")

                lock.withLock {
                    condition.signal()
                }
            }

            override fun onCancelledCallback(error: DatabaseError) {
                Log.d("onCancelledCallback", "invoked")
            }
        })
    }
    lock.withLock {
        condition.await()
    }

    Log.d("gianni", "15")
    return list
}*/