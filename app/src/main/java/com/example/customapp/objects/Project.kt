package com.example.customapp.objects

data class Project(val id: Int, var name: String, val block: Block? = null, val draft: Draft? = null, val internet: Internet? = null) {
}