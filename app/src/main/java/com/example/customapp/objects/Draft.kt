package com.example.customapp.objects

class Draft {
    private var ideas = mutableListOf<Idea>()

    fun add(name: String, text: String){
        ideas.add(Idea(ideas.size, name, Idea.NONE , Idea.OWN_IDEA, text))
    }

    fun remove(position: Int){
        ideas.removeAt(position)
    }

    fun edit(position: Int, name: String, text: String){
        ideas[position].apply {
            this.name = name
            this.text = text
        }
    }
}