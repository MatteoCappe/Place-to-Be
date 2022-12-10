package com.nomeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.nomeapp.R
import com.nomeapp.fragments.RegisterFragment
import com.nomeapp.models.FirebaseAuthWrapper

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_login, container, false)

        val thiz = this

        val button: Button = view.findViewById(R.id.loginButton)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val email: EditText = thiz.requireActivity().findViewById(R.id.userEmail)
                val password: EditText = thiz.requireActivity().findViewById(R.id.userPassword)

                if (email.text.isEmpty() || password.text.isEmpty()) {
                    email.setError("This is required")
                    password.setError("This is required")
                    return
                }

                action(email.text.toString(), password.text.toString())
            }

        })

        return view
    }

    fun action(email: String, password: String) {
        val firebaseAuthWrapper : FirebaseAuthWrapper = FirebaseAuthWrapper(this.requireContext())
        firebaseAuthWrapper.signIn(email, password)
    }

}
