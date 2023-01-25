package com.nomeapp.models

class User() {
    var userName: String = ""
    var Name: String = ""
    var Surname: String = ""
    var UserID: String = ""
    var Events: MutableList<Long>? = mutableListOf()
    var Followers: MutableList<String>? = mutableListOf() //? lista di uid
    var Following: MutableList<String>? = mutableListOf() //? lista di uid
    var Favourites: MutableList<Long>? = mutableListOf() //lista di EventID

    constructor(
        userName: String,
        Name: String,
        Surname: String,
        UserID: String,
        Events: MutableList<Long>?,
        Followers: MutableList<String>?,
        Following: MutableList<String>?,
        Favourites: MutableList<Long>?
    ) : this() {
        this.userName = userName
        this.Name = Name
        this.Surname = Surname
        this.UserID = UserID
        this.Events = Events
        this.Followers = Followers
        this.Following = Following
        this.Favourites = Favourites
    }
}