package com.example.customapp.objects

class Idea(private val id: Int, private var name: String, private var text:String) {

    fun change(name: String, text: String){
        this.name = name
        this.text = text
    }
}