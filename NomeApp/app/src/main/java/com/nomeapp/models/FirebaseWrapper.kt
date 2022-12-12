package com.nomeapp.models


//import androidx.test.core.app.ApplicationProvider.getApplicationContext
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nomeapp.activities.SplashActivity


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

    fun signUp(userName: String, email: String, password: String) {
        this.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success -> ask for permission
                val database = Firebase.database.reference
                val userid = getUid() //?????????
                writeDbUserName(userName, userid, database)
                //ovviamente non funziona ma non avevo dubbi
                //TODO save username on DB ora che abbiamo la schermata per metterlo
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(
                    context,
                    "Sign-up failed. Error message: ${task.exception!!.message}",
                    Toast.LENGTH_LONG
                ).show()
                //qui viene fuori l'errore dell'email già usata
            }
            //codice preso da internet per provare a capire come fare
            /*if (task.isSuccessful) {
                FirebaseUser = auth.getCurrentUser() //da qui sto mettendo roba di cui non sono molto sicuro
                user = FirebaseUser.getUid()
                //Log.e("id", user);
                reff.child(user).setValue(Employee)
                Toast.makeText(this@AddEmployee, "User Created.", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(
                        ApplicationProvider.getApplicationContext<Context>(),
                        AppStartActivity::class.java
                    )
                )
            } else {
                Toast.makeText(
                    this@AddEmployee,
                    "Error ! " + task.exception.getMessage(),
                    Toast.LENGTH_SHORT
                ).show()
            }*/
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
        //controllo anche su userName se non si riesce a capire come dare a login solo campi email e pw
    }

    private fun logSuccess() {
        val intent : Intent = Intent(this.context, SplashActivity::class.java)
        context.startActivity(intent)
    }

    //in toeria dovrebbe mettere lo userName dove lo userid è == user_id ma boh
    fun writeDbUserName(userName: String, userid: String?, database: DatabaseReference) {  //non penso sia molto sensato

        val user_id = database.child("user_id").toString()
        if (userid == user_id) {
            database.child("user_id").child("user").setValue(userName)  //ref.child(setValue(userName)) ?????
        }
        else {
            return; //se non succede c'è un qualche tipo di errore
        }

        //check if userid == $user_id ????
        // si può controllare usando auth.currentUser e getUid() del prof

        //only where userid == user_id
    }

}



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

    // TODO: merge
    lock.withLock {
        condition.await()
    }

    //for (myEvent : MyEvent in events) {
        //myEvent.isWeather = mapFirebaseEvents[myEvent.id]?.weather ?: myEvent.isWeather
        //myEvent.location = mapFirebaseEvents[myEvent.id]?.location ?: myEvent.location
    //}

}

// Database

// NB: For security reason update the access rules from firebase console --> an user can access on its data!
// rules: https://firebase.google.com/docs/rules/rules-and-auth?authuser=0
// doc: https://firebase.google.com/docs/rules/basics?utm_source=studio#realtime-database_5

class FirebaseDbWrapper(private val context: Context) {
    private val TAG: String = FirebaseDbWrapper::class.simpleName.toString()
    private val CHILD: String = "favourites"
    //possiamo usarlo anche per mettere eventi a cui uno è iscritto
    //mettendo String = "events"
    //events dovrebbe andare sotto users ma non sono troppo sicuro
    //example
/*"rules": {
    "users": {
      //lista userName //non so se vada anche qui il ".read" e il ".write", in teoria email e pw le prende da authentication
        "favourites": { //lista eventi seguiti
          "$uid": {
            ".read": "auth.uid === $uid",
            ".write": "auth.uid === $uid",
            }
          }
        }
    },*/

    private fun getDb() : DatabaseReference? {
        val ref = Firebase.database.getReference(CHILD)

        val uid = FirebaseAuthWrapper(context).getUid()
        if (uid == null) {
            return null;
        }

        return ref.child(uid)
    }

    fun writeDbData(firebaseFavourite: FirebaseFavourite) {
        val ref = getDb()

        if (ref == null) {
            return;
        }

        ref.child(firebaseFavourite.id.toString()).setValue(firebaseFavourite)
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

}*/