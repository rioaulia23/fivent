package com.example.fiventapp.Model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class EventModel(
    var key: String? = "",
    var iduser: String? = null,
    var id_event: String? = null,
    var event_name: String? = null,
    var userModel: UserModel? = null,
    var brosur: String? = null,
    var slot: String? = null,
    var phone: String? = null,
    var tersisa: String? = null,
    var category: String? = null,
    var domisili: String? = null,
    var participant: String? = null,
    var fav: Boolean? = false,
    var favkey: String? = "",
    var fee: String? = null,
    var open_register: String? = null,
    var close_register: String? = null,
    var place: String? = null,
    var email: String? = ""
) : Serializable