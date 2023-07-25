package com.example.customapp.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Block (
    val Pid:  String,
    val id: String,
    var name:String): Parcelable {
}