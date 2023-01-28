package com.nomeapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.nomeapp.activities.ShowEventActivity
import com.nomeapp.adapters.EventsAdapter
import com.nomeapp.models.SearchEvent
import com.nomeapp.models.SearchEvent
import kotlinx.coroutines.*

class SearchEventFragment(): Fragment() {
    var Title: String? = null
    var City: String? = null
    var Date: String? = null

    //TODO: checkbox per mostrare e nascondere campi di ricerca
    //search by: title, city, date, con tutte le possibili combinazioni

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("Title")?.let {
            Title = it
        }
        arguments?.getString("City")?.let {
            City = it
        }
        arguments?.getString("Date")?.let {
            Date = it
        }
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_searchevent, container, false)
        val fragmentManager = requireActivity().supportFragmentManager

        val ListOfEvents: ListView = view.findViewById(R.id.SearchEventList)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                val eventList = SearchEvent(this@SearchEventFragment.requireContext(), Title!!, City!!, Date!!)

                withContext(Dispatchers.Main) {
                    if (eventList.isEmpty()) {
                        fragmentManager.commit {
                            setReorderingAllowed(true)
                            val frag: Fragment = EventNotFoundFragment()
                            this.replace(R.id.SearchEventFragment, frag)
                        }
                    }
                    else {
                        val adapter = EventsAdapter(requireActivity(), eventList)
                        ListOfEvents.adapter = adapter
                        ListOfEvents.onItemClickListener =
                            AdapterView.OnItemClickListener { position, view, parent, id ->
                                val EventIDFromBox: Long =
                                    view.findViewById<TextView>(R.id.EventBox_ID).text.toString().toLong()
                                //TODO: check sulla data dell'evento prima di show
                                val intent: Intent = Intent(context, ShowEventActivity::class.java)
                                intent.putExtra("EventBoxID", EventIDFromBox)
                                startActivity(intent)
                            }
                    }
                }
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance (Title: String, City: String, Date: String) = SearchEventFragment().apply {
            arguments = Bundle().apply {
                putString("Title", Title)
                putString("City", City)
                putString("Date", Date)
            }
        }
    }
}