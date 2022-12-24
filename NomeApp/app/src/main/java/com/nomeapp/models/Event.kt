package com.nomeapp.models

import java.text.SimpleDateFormat
import java.util.*

class Event() {
    var Title: String = ""
    var eventID: Long = 0 //int?,  pensavo di mettere un n++ ad ogni evento creato
    var Date: Date = Date() //data e ora insieme
    var City: String = ""
    var Bio: String = ""
    var userID: String = "" //id creatore
    var userName: String = "" //username creatore, non credo serva
    //var Partecipanti: MutableList<String>? = mutableListOf() //dopo follow

    constructor(
        Title: String,
        eventID: Long, //int?, pensavo di mettere un n++ ad ogni evento creato
        Date: Date,
        City: String,
        Bio: String,
        userID: String,
        userName: String
        //Partecipanti: MutableList<String>? //vedi per ?
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