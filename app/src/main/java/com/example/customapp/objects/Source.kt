package com.example.customapp.objects

class Source(var title: String, var author: String, var year: String) {
    var ideas = mutableListOf<Idea>()
    var paraphrase = mutableListOf<Paraphrase>()

    fun add(name: String, type: Int, text: String){
        ideas.add(Idea(ideas.size, name, type, Idea.QUOTE,text))

    }

    fun  add(name: String, type: Int, text: String, paraphrase: String){
        ideas.add(Idea(ideas.size, name, type, Idea.PARAPHRASE,text))
        this.paraphrase.add(Paraphrase(ideas.size,paraphrase))
    }

    fun remove(position: Int){
        if (ideas[position].option == Idea.PARAPHRASE){
            val p = paraphrase.find{
                it.position == position
            }
            paraphrase.remove(p)
        }
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