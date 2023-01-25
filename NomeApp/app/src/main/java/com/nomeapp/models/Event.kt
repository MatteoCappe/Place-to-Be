package com.nomeapp.models

import java.text.SimpleDateFormat
import java.util.*

class Event() {
    var Title: String = ""
    var eventID: Long = 0
    var Date: Date = Date()
    var City: String = ""
    var Bio: String = ""
    var userID: String = "" //id creatore
    var userName: String = "" //username creatore, se serve
    //var Partecipanti: MutableList<String>? = mutableListOf() //dopo follow //TODO: rimuovi

    constructor(
        Title: String,
        eventID: Long,
        Date: Date,
        City: String,
        Bio: String,
        userID: String,
        userName: String
        //Partecipanti: MutableList<String>?
    ) : this() {
        this.Title = Title
        this.eventID = eventID
        this.Date = Date
        this.City = City
        this.Bio = Bio
        this.userID = userID
        this.userName = userName
        //this.Partecipanti = Partecipanti
    }

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    val formattedDate: String?
    get(): String? {
        return formatter.format(this.Date)
    }
}