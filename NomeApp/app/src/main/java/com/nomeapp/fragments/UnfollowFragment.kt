package com.nomeapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.nomeapp.models.*
import kotlinx.coroutines.*

class UnfollowFragment: Fragment() {
    //TODO: fix problema da importanzione della classe

    var UserID: String? = null
    var CurrentUser: User? = null
    var FollowedUser: User? = null
    val CurrentUserID: String? = FirebaseAuthWrapper(this@UnfollowFragment.requireContext()).getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString("UserID")?.let {
            UserID = it
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_unfollow, container, false)

        val unfollowButton: Button = view.findViewById<Button>(R.id.unfollowButton)

        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                FollowedUser = getUserByUsername(this@UnfollowFragment.requireContext(), UserID!!)
                CurrentUser = getMyData(this@UnfollowFragment.requireContext())

                withContext(Dispatchers.Main) {
                    unfollowButton.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View?) {
                            CurrentUser!!.Following!!.remove(FollowedUser!!.UserID)
                            FollowedUser!!.Followers!!.remove(CurrentUserID!!)
                        }
                    })
                }
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance (UserID: String) = UnfollowFragment().apply {
            arguments = Bundle().apply {
                putString("UserID", UserID)
            }
        }
    }

}