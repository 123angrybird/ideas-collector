package com.example.customapp.objects

class Internet {
    var source = mutableListOf<Source>()

    fun add(title: String, author: String, year: String){
        source.add(Source(title, author, year))
    }

    fun remove(position: Int){
        source.removeAt(position)
    }

    fun edit(position: Int, title: String, author: String, year: String){
        source[position].apply {
            this.title = title
            this.author = author
            this.year = year
        }
    }
}