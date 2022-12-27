package com.nomeapp.models

class User() {
    var userName: String = ""
    var Name: String = ""
    var Surname: String = ""
    var UserID: String = ""
    //var CreatedEvents: MutableList<String>? = mutableListOf()
    //var Favourites: MutableList<String>? = mutableListOf() //lista di EventID
    //var Followers: MutableList<String>? = mutableListOf() //? lista di uid
    //var Following: MutableList<String>? = mutableListOf() //? lista di uid

    constructor(
        userName: String,
        Name: String,
        Surname: String,
        UserID: String//,
        //CreatedEvents: MutableList<String>?,
        //Favourites: MutableList<String>?,
        //Followers: MutableList<String>?,
        //Following: MutableList<String>?
    ) : this() {
        this.userName = userName
        this.Name = Name
        this.Surname = Surname
        this.UserID = UserID
        //this.CreatedEvents = CreatedEvents
        //this.Favourites = Favourites
        //this.Followers = Followers
        //this.Following = Following
    }
}