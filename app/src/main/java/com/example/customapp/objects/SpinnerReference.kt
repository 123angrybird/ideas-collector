package com.example.customapp.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpinnerReference(
    val what: String,
    val name: String,
    val text: String,
    val option: Int): Parcelable {
}