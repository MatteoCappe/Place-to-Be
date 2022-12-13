package com.nomeapp.models

class User() {
    var userName: String = ""
    var Name: String = ""
    var Surname: String = ""

    constructor(
        userName: String,
        Name: String,
        Surname: String
        //poi ci saranno da aggiungere followers e events????
        //var Followers: MutableList<String>? = mutableListOf()
        //var Events: MutableList<String>? = mutableListOf()
        //var Favourites: MutableList<String>? = mutableListOf() //???
    ) : this() {
        this.userName = userName
        this.Name = Name
        this.Surname = Surname
        //this.Followers = Followers
        //this.Events = Events
        //this.Favourites = Favourites
    }
}