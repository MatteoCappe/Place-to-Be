package com.nomeapp.models

import android.content.Context
import com.example.nomeapp.R
import android.content.Context.MODE_PRIVATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default.nextInt

fun runInstantWorker(context: Context) {
    val followerNotificationWorker = OneTimeWorkRequestBuilder<followerNotificationWorker>().build()
    WorkManager.getInstance(context).enqueue(followerNotificationWorker)
    startWorker(context)
}

fun startWorker(context: Context) {
    val DB = FirebaseDbWrapper(context).ref
    GlobalScope.launch {
        DB.child("followers").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val followerNotificationWorker = OneTimeWorkRequestBuilder<followerNotificationWorker>().build()
                WorkManager.getInstance(context).enqueue(followerNotificationWorker)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

class followerNotificationWorker(val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {
        val followers = getFollowers(context)
        var notificationID = 0

        if (followers.isNotEmpty()) {
            for (follower in followers) {
                val notificationText = "${follower.userName} ha iniziato a seguirti!"
                val builder = NotificationCompat.Builder(context, "FOLLOWER")
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Follower")
                    .setContentText(notificationText)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true) //TODO: vedi se si riesce a rimandare al profilo

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "follower"
                    val descriptionText = "follower alert"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel("FOLLOWER", name, importance).apply{
                        description = descriptionText
                    }

                    val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }

                with(NotificationManagerCompat.from(context)) {
                    notify(notificationID, builder.build())
                    notificationID += 1 //TODO: check
                }
            }
        }

        return Result.success()
    }
}