package com.nomeapp.adapters

import android.app.usage.UsageEvents
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.nomeapp.R
import com.nomeapp.models.Event
import com.nomeapp.models.FirebaseStorageWrapper
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class EventsAdapter (context: Context, val events: List<Event>):
    ArrayAdapter<Event>(context, R.layout.event_infobox, events) {

    //potrebbe servire per follower etc
    override fun getCount(): Int {
        Log.d("users num", events.size.toString())
        return events.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val event: Event = events[position]
        var view: View? = convertView

        val dateFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm")

        val currentTime = Calendar.getInstance()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.US)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_infobox, parent, false)
        }

        val ExpiredEvent: TextView = view!!.findViewById(R.id.Expired)
        if (LocalDateTime.parse(event!!.formattedDate, formatter).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli() < currentTime.timeInMillis) {
            ExpiredEvent.setVisibility(View.VISIBLE)
        }

        val EventBoxEventID: TextView = view!!.findViewById(R.id.EventBox_ID)
        EventBoxEventID.text = event.eventID.toString()

        val EventBoxTitle: TextView = view!!.findViewById(R.id.EventBox_Title)
        EventBoxTitle.text = event.Title

        val EventBoxCity: TextView = view.findViewById(R.id.EventBox_City)
        EventBoxCity.text = event.City

        val EventBoxDate: TextView = view.findViewById(R.id.EventBox_Date)
        EventBoxDate.text = dateFormatter.format(event.Date)

        val EventBoxPhoto: ImageView = view.findViewById(R.id.EventBox_Photo)
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                val image = FirebaseStorageWrapper(context).downloadEventImage(events[position].eventID.toString())

                withContext(Dispatchers.Main) {
                    if (image != null) {
                        EventBoxPhoto.setImageURI(image)
                    }
                }
            }
        }

        return view
    }

}