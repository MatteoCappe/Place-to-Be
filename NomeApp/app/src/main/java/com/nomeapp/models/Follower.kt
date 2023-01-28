package com.nomeapp.models

class Follower() {
    var userName: String = ""
    var UserID: String = ""

    constructor(
        userName: String,
        UserID: String
    ) : this() {
        this.userName = userName
        this.UserID = UserID
    }
}