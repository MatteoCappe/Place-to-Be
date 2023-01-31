package com.nomeapp.models

import android.content.Context
import com.example.nomeapp.R
import android.content.Context.MODE_PRIVATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.nomeapp.activities.ShowProfileActivity
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default.nextInt

fun runInstantWorker(context: Context) {
    Log.d("gianni", "16")
    val followerNotificationWorker = OneTimeWorkRequestBuilder<followerNotificationWorker>().build()
    WorkManager.getInstance(context).enqueue(followerNotificationWorker)
    Log.d("gianni", "17")
    startWorker(context)
    startPeriodicWorker(context)
}

fun startWorker(context: Context) {
    Log.d("gianni", "18")
    val DB = FirebaseDbWrapper(context).ref
    val uid = FirebaseAuthWrapper(context).getUid()
    GlobalScope.launch {
        DB.child("followers").child(uid!!).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("gianni", "19")
                val followerNotificationWorker = OneTimeWorkRequestBuilder<followerNotificationWorker>().build()
                WorkManager.getInstance(context).enqueue(followerNotificationWorker)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}

fun startPeriodicWorker(context: Context) {
    val followerPeriodicNotificationWorker =
        PeriodicWorkRequestBuilder<followerNotificationWorker>(15, TimeUnit.MINUTES).build()
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork("myPeriodicWork", ExistingPeriodicWorkPolicy.KEEP, followerPeriodicNotificationWorker)
}

class followerNotificationWorker(val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val uid = FirebaseAuthWrapper(context).getUid()
        val followers = getFollowers(context)
        var notificationID = 0
        Log.d("gianni", "22")

        //TODO: capisci come farlo funzionare
        if (followers.isNotEmpty()) {
            Log.d("gianni", "23")
            for (follower in followers) {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        val user = getUserByID(context, follower)
                        withContext(Dispatchers.Main) {
                            Log.d("gianni", "25")
                            val notificationText = "${user.userName} ha iniziato a seguirti!"
                            val builder = NotificationCompat.Builder(context, "FOLLOWER")
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle("Follower")
                                .setContentText(notificationText)
                                .setStyle(
                                    NotificationCompat.BigTextStyle().bigText(notificationText)
                                )
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setAutoCancel(true)

                            //TODO: check
                            val intent = Intent(context, ShowProfileActivity::class.java)
                            intent.putExtra("UserBoxUsername", user.userName)

                            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val name = "follower"
                                val descriptionText = "follower alert"
                                val importance = NotificationManager.IMPORTANCE_DEFAULT
                                val channel =
                                    NotificationChannel("FOLLOWER", name, importance).apply {
                                        description = descriptionText
                                    }

                                val notificationManager: NotificationManager =
                                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.createNotificationChannel(channel)
                            }


                            with(NotificationManagerCompat.from(context)) {
                                notify(notificationID, builder.build())
                                notificationID += 1 //TODO: fai come eventID (firebase -> geteventid)
                                Log.d("gianni notifiche", notificationID.toString())
                            }
                            Firebase.database.getReference("followers").child(uid!!).child(follower).removeValue()

                        }
                    }
                }
            }
        }

        return Result.success()
    }
}