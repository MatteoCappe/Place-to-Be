package com.nomeapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.nomeapp.activities.ShowProfileActivity
import com.nomeapp.adapters.UsersAdapter
import com.nomeapp.models.User
import com.nomeapp.models.getUsersByUsernameStart
import kotlinx.coroutines.*

class SearchUserFragment(): Fragment() {
    var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("userName")?.let {
            userName = it
        }
    }

    override fun onCreateView (inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_searchuser, container, false)
        val fragmentManager = requireActivity().supportFragmentManager

        val ListOfUsers: ListView = view.findViewById(R.id.SearchUserList)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                val userList = getUsersByUsernameStart(this@SearchUserFragment.requireContext(), userName!!)

                withContext(Dispatchers.Main) {
                    if (userList.isEmpty()) {
                        fragmentManager.commit {
                            setReorderingAllowed(true)
                            val frag: Fragment = UsernameNotFoundFragment()
                            this.replace(R.id.SearchUserFragment, frag)
                        }
                    }
                    else {
                        val adapter = UsersAdapter(requireActivity(), userList)
                        ListOfUsers.adapter = adapter
                        ListOfUsers.onItemClickListener =
                            AdapterView.OnItemClickListener { position, view, parent, id ->
                                val UsernameFromUserBox = view.findViewById<TextView>(R.id.UserBox_Username)
                                val intent: Intent = Intent(context, ShowProfileActivity::class.java)
                                intent.putExtra("UserBoxUsername", UsernameFromUserBox.text.toString())
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
        fun newInstance (userName: String) = SearchUserFragment().apply {
            arguments = Bundle().apply {
                putString("userName", userName)
            }
        }
    }
}