package com.nomeapp.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import java.text.SimpleDateFormat
import java.util.*

class SearchEventFragment: Fragment() {
    private var myCalendar : Calendar= Calendar.getInstance()

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_searchevent, container, false)
        val fragmentManager = requireActivity().supportFragmentManager

        val SearchEventButton: Button = view.findViewById(R.id.SearchEventButton) as Button
        val ClearDate: ImageView = view.findViewById(R.id.ClearDate) as ImageView

        val Title: EditText = view.findViewById(R.id.searchTitle) as EditText
        val City: EditText = view.findViewById(R.id.searchCity) as EditText
        val Date: EditText = view.findViewById(R.id.searchDate) as EditText

        //TODO: checkbox per mostrare e nascondere campi di ricerca
        //search by: title, city, date, con tutte le possibili combinazioni

        val DateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US) //vedi se il formato della data va bene quando si aggiungerà ricerca
                Date.setText(dateFormat.format(myCalendar.time))
            }

        Date.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(
                    this@SearchEventFragment.requireActivity(),
                    DateListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            }
        })

        ClearDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Date.text.clear()
            }
        })

        SearchEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (Title.text.isEmpty() && City.text.isEmpty() && Date.text.isEmpty()) {
                    Title.setError(getString(R.string.eventSearchError))
                    City.setError(getString(R.string.eventSearchError))
                    Date.setError(getString(R.string.eventSearchError))
                }

                else {
                    fragmentManager.commit {
                        setReorderingAllowed(true)
                        val frag: Fragment = EventListFragment.newInstance(
                            Title.text.toString(),
                            City.text.toString(),
                            Date.text.toString()
                        )
                        replace(R.id.SearchEventFragment, frag)
                    }
                }
            }
        })

        return view
    }

    //usato per risolvere un problema che si presentava dopo essere tornti indietro da showEvent
    override fun onResume() {
        super.onResume()

        val Title: EditText = requireView().findViewById<View>(R.id.searchTitle) as EditText
        val City: EditText = requireView().findViewById<View>(R.id.searchCity) as EditText
        val Date: EditText = requireView().findViewById<View>(R.id.searchDate) as EditText

        if (!(Title.text.isEmpty() && City.text.isEmpty() && Date.text.isEmpty())) {
            requireFragmentManager().commit {
                setReorderingAllowed(true)
                val frag: Fragment = EventListFragment.newInstance(
                    Title.text.toString(),
                    City.text.toString(),
                    Date.text.toString()
                )
                replace(R.id.SearchEventFragment, frag)
            }
        }

        val SearchEventButton: Button = requireView().findViewById<View>(R.id.SearchEventButton) as Button
        val ClearDate: ImageView = requireView().findViewById<View>(R.id.ClearDate) as ImageView

        val DateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                Date.setText(dateFormat.format(myCalendar.time))
            }

        Date.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view: View?) {
                DatePickerDialog(
                    this@SearchEventFragment.requireActivity(),
                    DateListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            }
        })

        ClearDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Date.text.clear()
            }
        })

        SearchEventButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (Title.text.isEmpty() && City.text.isEmpty() && Date.text.isEmpty()) {
                    Title.setError(getString(R.string.eventSearchError))
                    City.setError(getString(R.string.eventSearchError))
                    Date.setError(getString(R.string.eventSearchError))
                }

                else {
                    requireFragmentManager().commit {
                        setReorderingAllowed(true)
                        val frag: Fragment = EventListFragment.newInstance(
                            Title.text.toString(),
                            City.text.toString(), Date.text.toString()
                        )
                        replace(R.id.SearchEventFragment, frag)
                    }
                }
            }
        })
    }

}