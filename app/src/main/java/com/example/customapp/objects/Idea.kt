package com.example.customapp.objects

class Idea(
    private val id: Int,
    var name: String,
    var type: Int = NONE,
    var option: Int = OWN_IDEA,
    var text: String
) {

    companion object {
        const val NONE = 0
        const val FIRST_TOPIC_SENTENCE = 1
        const val SUB_TOPIC = 2
        const val EXPLAIN = 3
        const val ANALYZE = 4
        const val EXAMPLE = 5

        const val OWN_IDEA = 0
        const val PARAPHRASE = 1
        const val QUOTE = 2
    }
}