package com.nomeapp.models

class Follower() {
    var UserID: String = ""
    var Username: String = ""

    constructor(
        UserID: String,
        Username: String
    ) : this() {
        this.UserID = UserID
        this.Username = Username
    }
}