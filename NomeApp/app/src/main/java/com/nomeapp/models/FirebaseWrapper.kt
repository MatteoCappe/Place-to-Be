package com.nomeapp.models


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.nomeapp.activities.SplashActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

class FirebaseAuthWrapper(private val context: Context) {
    private val TAG : String = FirebaseAuthWrapper::class.simpleName.toString()
    private var auth: FirebaseAuth = Firebase.auth

    fun isAuthenticated() : Boolean {
        return auth.currentUser != null
    }

    fun getUid() : String? {
        return auth.currentUser?.uid
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

    fun writeDbFollower(UserID: String, followerUID: String) {
        ref.child("followers").child(UserID).setValue(followerUID)
    }

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
        storageRef.child("events/${eventID}.jpg").putFile(eventImage) //TODO: check
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

    //TODO: check data db > data attuale prima di show
    //se si rompe è per l'implementazine del check sulla data

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (events in snapshot.child("events").children) {
                    val event = events.getValue(Event::class.java)

                    if (Title.length > 0 && City.length == 0 && Date.length == 0) {
                        if (event!!.Title.startsWith(Title, true)) {
                            eventList.add(event!!)
                        }
                    }

                    else if (City.length > 0 && Title.length == 0 && Date.length == 0) {
                        if (event!!.City.lowercase().equals(City.lowercase())) {
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
                            event!!.City.lowercase().equals(City.lowercase())) {
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

                    //TODO: metti data < DBData amziche uguale in quelle con più campi?
                    //in quello only data lascia uguale

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

/*fun DeleteFollower (context: Context, followerUID: String) {
    val userID = FirebaseAuthWrapper(context).getUid()
    val lock = ReentrantLock()
    val condition = lock.newCondition()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")

                snapshot.child("followers").child(userID!!).child(followerUID).ref.removeValue()

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
}*/

fun SendFollow (context: Context, followerUID: String) {
    val userID = FirebaseAuthWrapper(context).getUid()
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var followersUID: MutableList<String> = ArrayList()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                followersUID = snapshot.child("followers").child(userID!!).value as MutableList<String>
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

    FirebaseDbWrapper(context).ref.child("followers").child(userID!!).setValue(followersUID)

}

fun getFollowers(context: Context): MutableList<User> {
    val userID = FirebaseAuthWrapper(context).getUid()

    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var list: MutableList<String> = ArrayList()
    var followerList: MutableList<User> = ArrayList()
    var user: User? = null

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")

                val followers = snapshot.child("followers").child(userID!!).children
                for (follower in followers) {
                    list.add(follower.toString())
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

    //da uid a user
    if (list.isNotEmpty()) {
        GlobalScope.launch {
            FirebaseDbWrapper(context).readDbData(object :
                FirebaseDbWrapper.Companion.FirebaseReadCallback {
                override fun onDataChangeCallback(snapshot: DataSnapshot) {
                    Log.d("onDataChangeCallback", "invoked")

                    val users = snapshot.child("users").children
                    for (uid in users) {
                        if (list.contains(uid.child("userID").getValue(String::class.java)!!)) {
                            user = uid.getValue(User::class.java)
                            followerList.add(user!!)
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
    }

    return followerList
}