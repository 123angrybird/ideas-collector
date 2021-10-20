package com.example.customapp.objects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Idea(
    val bId: String,
    val id: String,
    var text: String,
    var type: Int = NONE,
    var option: Int = OWN_IDEA,
    var expand: Boolean = false
): Parcelable {

    companion object {
        const val NONE = 0
        const val MAIN_TOPIC = 1
        const val SUB_TOPIC = 2
        const val EXPLAIN = 3
        const val PROOF = 4
        const val ANALYZE = 5
        const val EXAMPLE = 6

        const val OWN_IDEA = 0
        const val PARAPHRASE = 1
        const val QUOTE = 2

        fun convertTypeToString(value: Int): String{
            return when(value){
                1 -> "Main Topic"
                2 -> "Subtopic"
                3 -> "Explain"
                4 -> "Proof"
                5 -> "Analyze"
                6 -> "Example"
                else -> "None"
            }
        }

        fun convertTypeToValue(type: String): Int{
            return when (type){
                "Main Topic"    -> 1
                "Subtopic"      -> 2
                "Explain"       -> 3
                "Proof"         -> 4
                "Analyze"       -> 5
                "Example"       -> 6
                else    -> 0
            }
        }
    }
}