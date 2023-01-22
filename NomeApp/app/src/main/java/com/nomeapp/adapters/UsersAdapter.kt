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

class UsersAdapter (context: Context, val resource: Int, val users: List<User>):
    ArrayAdapter<User>(context, resource, users) {

    //potrebbe servire per follower etc
    /*override fun getCount(): Int {
        Log.d("users num", users.size.toString())
        return users.size
    }*/

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //val user: User = users[position]
        var view: View? = convertView

        Log.d("users check", "gianni")

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.user_infobox, parent, false)
        }

        var count: Int = 0

        for (user in users) {



            Log.d("users array", user.userName + " " + count.toString() )

            val UserBoxUsername: TextView = view!!.findViewById(R.id.UserBox_Username)
            val UserBoxName: TextView = view.findViewById(R.id.UserBox_Name)
            val UserBoxSurname: TextView = view.findViewById(R.id.UserBox_Surname)
            val UserBoxPhoto: ImageView = view.findViewById(R.id.UserBox_Photo)

            UserBoxUsername.text = user.userName
            UserBoxName.text = user.Name
            UserBoxSurname.text = user.Surname

            CoroutineScope(Dispatchers.Main + Job()).launch {
                withContext(Dispatchers.IO) {
                    val image = FirebaseStorageWrapper(context).downloadUserImage(user.UserID)

                    withContext(Dispatchers.Main) {
                        if (image != null) {
                            UserBoxPhoto.setImageURI(image)
                        }
                    }
                }
            }

            count += 1
        }

        Log.d("users check view", view.toString())

        //non sono sicuro del return
        return view!!
    }

}