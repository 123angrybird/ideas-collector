package com.example.customapp.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InternetIdea(
    val sid: String,
    val pid: String,
    val id: String,
    var name: String,
    var text: String,
    var option: Int = Idea.QUOTE,
    var paraphrase: String,
    var expand: Boolean = false): Parcelable {
}