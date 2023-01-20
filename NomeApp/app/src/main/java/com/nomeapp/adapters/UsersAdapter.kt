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
    /*override fun getCount(): Int {
        Log.d("users num", users.size.toString())
        return users.size
    }*/

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //val view: View? = convertView

        //if view == null {inflater}??????
        val user: User = users[position]
        Log.d("users[position]", user.userName)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view: View = inflater.inflate(R.layout.user_infobox, parent, false)

        Log.d("users position", position.toString())

        val UserBoxPhoto: ImageView = view.findViewById(R.id.UserBox_Photo)
        val UserBoxUsername: TextView = view.findViewById(R.id.UserBox_Username)
        val UserBoxName: TextView = view.findViewById(R.id.UserBox_Name)
        val UserBoxSurname: TextView = view.findViewById(R.id.UserBox_Surname)

        UserBoxUsername.text = users[position].userName
        UserBoxName.text = users[position].Name
        UserBoxSurname.text = users[position].Surname

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

        //non sono sicuro del return
        return view
    }

}