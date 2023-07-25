package com.example.customapp.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Draft (
    val Pid:  String,
    val id: String,
    var name:String,
    var text: String,
    var expand: Boolean = false): Parcelable {
}