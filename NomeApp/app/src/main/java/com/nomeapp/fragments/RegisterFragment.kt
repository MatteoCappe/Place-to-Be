package com.nomeapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.nomeapp.R
import com.nomeapp.fragments.LoginFragment
import com.nomeapp.models.FirebaseAuthWrapper

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_register, container, false)

        val thiz = this

        val button: Button = view.findViewById(R.id.registerButton)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val userName: EditText = thiz.requireActivity().findViewById(R.id.userName)
                val email: EditText = thiz.requireActivity().findViewById(R.id.userEmail)
                val password: EditText = thiz.requireActivity().findViewById(R.id.userPassword)

                if (userName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty()) {
                    userName.setError("This is required")
                    email.setError("This is required")
                    password.setError("This is required")
                    return
                }

                action(userName.text.toString(), email.text.toString(), password.text.toString())
            }

        })

        return view
    }

    fun action(userName: String, email: String, password: String) {
        val firebaseAuthWrapper : FirebaseAuthWrapper = FirebaseAuthWrapper(this.requireContext())
        firebaseAuthWrapper.signUp(userName, email, password)
    }
}