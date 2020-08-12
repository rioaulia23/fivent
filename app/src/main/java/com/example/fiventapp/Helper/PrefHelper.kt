package com.example.fiventapp.Helper

import android.content.Context
import android.content.SharedPreferences

class PrefHelper {
    val COUNTER_ID = "counter"
    val USER_ID = "uidx"
    val statusPenjual = "STATUS_PENJUAL"
    val statusPembeli = "STATUS_PEMBELI"


    var mContext: Context
    var sharedSet: SharedPreferences

    constructor(ctx: Context) {
        mContext = ctx
        sharedSet = mContext.getSharedPreferences(
            "DB",
            Context.MODE_PRIVATE
        )
    }

    fun saveUID(uid: String) {
        val edit = sharedSet.edit()
        edit.putString(USER_ID, uid)
        edit.apply()
    }

    fun setStatusPenjual(status: Boolean) {
        val edit = sharedSet.edit()
        edit.putBoolean(statusPenjual, status)
        edit.apply()
    }

    fun setStatusPembeli(status: Boolean) {
        val edit = sharedSet.edit()
        edit.putBoolean(statusPembeli, status)
        edit.apply()
    }

    fun getUID(): String? {
        return sharedSet.getString(USER_ID, "")
    }

    fun saveCounterId(counter: Int) {
        val edit = sharedSet.edit()
        edit.putInt(COUNTER_ID, counter)
        edit.apply()
    }

    fun getCounterId(): Int {
        return sharedSet.getInt(COUNTER_ID, 1)
    }

    fun cekStatusPenjual(): Boolean? {
        return sharedSet.getBoolean(statusPenjual, false)
    }

    fun cekStatusPembeli(): Boolean? {
        return sharedSet.getBoolean(statusPembeli, false)
    }


}