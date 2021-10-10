package com.example.customapp.objects

class Block {
    var ideas = mutableListOf<Idea>()

    fun add(name: String, type: Int, text: String){
        ideas.add(Idea(ideas.size, name, type, Idea.OWN_IDEA,text))
    }

    fun remove(position: Int){
        ideas.removeAt(position)
    }

    fun edit(position: Int, name: String, type: Int, text: String){
        ideas[position].apply {
            this.name = name
            this.type = type
            this.text = text
        }
    }
}