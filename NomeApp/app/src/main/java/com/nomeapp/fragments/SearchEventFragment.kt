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
import com.nomeapp.models.getEventsByTitleStart
import kotlinx.coroutines.*

class SearchEventFragment(): Fragment() {
    var Title: String? = null
    //TODO: metti città e data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("Title")?.let {
            Title = it
        }
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_searchevent, container, false)
        val fragmentManager = requireActivity().supportFragmentManager

        val ListOfEvents: ListView = view.findViewById(R.id.SearchEventList)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                val eventList = getEventsByTitleStart(this@SearchEventFragment.requireContext(), Title!!)

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
        fun newInstance (Title: String) = SearchEventFragment().apply {
            arguments = Bundle().apply {
                putString("Title", Title)
            }
        }
    }

    //TODO: rimuovi implemenazioni inutili
}