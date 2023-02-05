package com.nomeapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nomeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nomeapp.activities.MainActivity
import com.nomeapp.activities.UpdateMyEventActivity
import com.nomeapp.models.DeleteEvent
import kotlinx.coroutines.*

class ShowMyEventFragment(): Fragment() {
    var eventID: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_showmyevent, container, false)

        arguments?.getLong("eventID")?.let {
            eventID = it
        }

        val EditEventButton: FloatingActionButton = view.findViewById(R.id.EventFragment_EditEventButton)
        val DeleteEventButton: FloatingActionButton = view.findViewById(R.id.EventFragment_DeleteEventButton)

        EditEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Intent(context, UpdateMyEventActivity::class.java)
                intent.putExtra("eventID", eventID)
                startActivity(intent)
            }
        })

        DeleteEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val builder = AlertDialog.Builder(this@ShowMyEventFragment.requireActivity())
                builder.setMessage("Vuoi davvero cancellare questo evento?")
                    .setCancelable(false)
                    .setPositiveButton("Sì") { dialog, id ->
                        CoroutineScope(Dispatchers.Main + Job()).launch {
                            withContext(Dispatchers.IO) {
                                DeleteEvent(this@ShowMyEventFragment.requireContext(), eventID!!)

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Evento cancellato con successo!", Toast.LENGTH_LONG).show()
                                    val intent: Intent = Intent(context, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance (eventID: Long) = ShowMyEventFragment().apply {
            arguments = Bundle().apply {
                putLong("eventID", eventID)
            }
        }
    }
}