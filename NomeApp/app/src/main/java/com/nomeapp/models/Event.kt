package com.nomeapp.models

import java.util.*

class Event() {
    var eventTitle: String = ""
    //var eventID: String = "" //int?,  pensavo di mettere un n++ ad ogni evento creato
    //var Date: Date = Date()
    //var Time: Time = Time() //????
    var City: String = ""
    var Bio: String = ""
    var userID: String = "" //id creatore
    //var userName: String = "" //username creatore, non credo serva
    //var Partecipanti: MutableList<String>? = mutableListOf()

    constructor(
        eventTitle: String,
        //eventID: String, //int?, pensavo di mettere un n++ ad ogni evento creato
        //Date: Date,
        //Time: Time
        City: String,
        Bio: String,
        userID: String,
        //userName: String
        //Partecipanti: MutableList<String>? //vedi per ?
    ) : this() {
        this.eventTitle = eventTitle
        //this.eventID = eventID
        //this.Date = Date
        //this.Time = Time
        this.City = City
        this.Bio = Bio
        this.userID = userID
        //this.userName = userName
        //this.Partecipanti = Partecipanti
    }
}