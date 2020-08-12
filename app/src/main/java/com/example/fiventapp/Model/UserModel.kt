package com.example.fiventapp.Model

import java.io.Serializable

class UserModel(
    var key: String,
    var id: String,
    var name: String,
    var password: String,
    var phone: String,
    var email: String,
    var ava: String
) : Serializable {
    constructor() : this("", "", "", "", "", "", "")
}