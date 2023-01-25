package com.nomeapp.adapters

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
import com.nomeapp.models.FirebaseStorageWrapper
import com.nomeapp.models.User
import kotlinx.coroutines.*

class UsersAdapter (context: Context, val users: List<User>):
    ArrayAdapter<User>(context, R.layout.user_infobox, users) {

    //potrebbe servire per follower etc
    override fun getCount(): Int {
        return users.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val user: User = users[position]
        var view: View? = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.user_infobox, parent, false)
        }

        val UserBoxUsername: TextView = view!!.findViewById(R.id.UserBox_Username)
        UserBoxUsername.text = user.userName

        val UserBoxName: TextView = view.findViewById(R.id.UserBox_Name)
        UserBoxName.text = user.Name

        val UserBoxSurname: TextView = view.findViewById(R.id.UserBox_Surname)
        UserBoxSurname.text = user.Surname

        val UserBoxPhoto: ImageView = view.findViewById(R.id.UserBox_Photo)
        CoroutineScope(Dispatchers.Main + Job()).launch {
            withContext(Dispatchers.IO) {
                val image = FirebaseStorageWrapper(context).downloadUserImage(users[position].UserID)

                withContext(Dispatchers.Main) {
                    if (image != null) {
                        UserBoxPhoto.setImageURI(image)
                    }
                }
            }
        }

        return view
    }

}