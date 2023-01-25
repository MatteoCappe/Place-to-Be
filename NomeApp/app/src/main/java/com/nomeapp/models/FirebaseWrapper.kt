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
import java.util.concurrent.locks.ReentrantLock
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

    //logOut() {auth.signOut()}, vedi (comodo magari per evitare di disinstallare e reinstallare ogni volta)

    //delete?
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
    Log.d("followers", "boh empty")
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

    //check per non cercare se stessi?? (troppo uguale a quello di yasso) //TODO: eeehhhh nascondi le prove
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

fun getEventsByTitleStart (context: Context, Title: String): MutableList<Event> {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    var eventList: MutableList<Event> = ArrayList()

    GlobalScope.launch {
        FirebaseDbWrapper(context).readDbData(object :
            FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")
                for (events in snapshot.child("events").children) {
                    val event = events.getValue(Event::class.java)
                    if (event!!.Title.startsWith(Title, true)) {
                        eventList.add(event!!)
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

//vedi per favorite, versione vecchissima probabilmente sbagliata
/*fun mergeMyFavouritesWithFirebaseInfo (context: Context, favourites: List<MyFavourites>) {
    val lock = ReentrantLock()
    val condition = lock.newCondition()

    val mapFirebaseFavourites = HashMap<Long, FirebaseDbWrapper.Companion.FirebaseFavourite>()
    GlobalScope.launch{
        FirebaseDbWrapper(context).readDbData(object : FirebaseDbWrapper.Companion.FirebaseReadCallback {
            override fun onDataChangeCallback(snapshot: DataSnapshot) {
                Log.d("onDataChangeCallback", "invoked")

                for (child in snapshot.children){
                    val firebaseEvent : FirebaseDbWrapper.Companion.FirebaseFavourite = child.getValue(FirebaseDbWrapper.Companion.FirebaseFavourite::class.java)!!
                    mapFirebaseFavourites.put(firebaseFavourite.id, firebaseFavourite)
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

    //for (myEvent : MyEvent in events) {
        //myEvent.isWeather = mapFirebaseEvents[myEvent.id]?.weather ?: myEvent.isWeather
        //myEvent.location = mapFirebaseEvents[myEvent.id]?.location ?: myEvent.location
    //}

}

    companion object {
        class FirebaseFavourite() {
            var id : Long = -1 //?
            var luogo: String = ""
            var data: ...
            var descrizione: String = ""
            var IdProfile: ...
            //lista Id dei partrecipanti ??

            constructor(id: Long, weather: Boolean, location: String) : this() {
                this.id = id
                this.luogo = location
                this.descrizione = descrizione
                this.IdProfile = IdProfile
                this.data = data
                //lista Id dei partrecipanti ??

            }
        }
}*/