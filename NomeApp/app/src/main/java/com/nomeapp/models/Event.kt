package com.nomeapp.models

import java.text.SimpleDateFormat
import java.util.*

class Event() {
    var Title: String = ""
    var eventID: Long = 0
    var Date: Date = Date()
    var City: String = ""
    var Address: String = ""
    var Bio: String = ""
    var userID: String = ""
    var userName: String = ""

    constructor(
        Title: String,
        eventID: Long,
        Date: Date,
        City: String,
        Address: String,
        Bio: String,
        userID: String,
        userName: String
    ) : this() {
        this.Title = Title
        this.eventID = eventID
        this.Date = Date
        this.City = City
        this.Address = Address
        this.Bio = Bio
        this.userID = userID
        this.userName = userName
    }

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    val formattedDate: String?
    get(): String? {
        return formatter.format(this.Date)
    }
}