package com.nomeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R

class SearchUserFragment: Fragment() {

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_searchuser, container, false)
        val fragmentManager = requireActivity().supportFragmentManager

        val SearchUserButton: Button = view.findViewById(R.id.SearchUserButton) as Button

        SearchUserButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var userName: EditText = view.findViewById<View>(R.id.searchUserName) as EditText

                if (userName.text.isEmpty()) {
                    userName.setError("This is required")
                }

                else {
                    fragmentManager.commit {
                        setReorderingAllowed(true)
                        val frag: Fragment = UserListFragment.newInstance(userName.text.toString())
                        replace(R.id.SearchUserFragment, frag)
                    }
                }
            }
        })

        return view
    }

    //usato per risolvere un problema che si presentava dopo essere tornti indietro da showProfile
    override fun onResume() {
        super.onResume()

        var userName: EditText = requireView().findViewById<View>(R.id.searchUserName) as EditText

        if (!userName.text.isEmpty()) {
            requireFragmentManager().commit {
                setReorderingAllowed(true)
                val frag: Fragment = UserListFragment.newInstance(userName.text.toString())
                replace(R.id.SearchUserFragment, frag)
            }
        }

        val SearchUserButton: Button = requireView().findViewById<View>(R.id.SearchUserButton) as Button

        SearchUserButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var userName: EditText = requireView().findViewById<View>(R.id.searchUserName) as EditText

                if (userName.text.isEmpty()) {
                    userName.setError(getString(R.string.emptyError))
                }

                else {
                    requireFragmentManager().commit {
                        setReorderingAllowed(true)
                        val frag: Fragment = UserListFragment.newInstance(userName.text.toString())
                        replace(R.id.SearchUserFragment, frag)
                    }
                }
            }
        })
    }

}