package com.example.customapp.objects

class Draft() {
    private var ideas = mutableListOf<Idea>()

    fun add(idea: Idea){
        ideas.add(idea)
    }

    fun remove(position: Int){
        ideas.removeAt(position)
    }

    fun edit(position: Int, name: String, text: String){
        ideas
    }
}