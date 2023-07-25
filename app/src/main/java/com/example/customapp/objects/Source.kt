package com.example.customapp.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Source(
    val Pid: String,
    val id: String,
    var title: String,
    var author: String,
    var year: Int): Parcelable {}