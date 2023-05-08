package com.example.trivia.game

import com.example.trivia.database.QuestionTrivia
import kotlin.random.Random

class TriviaProcessor {
    private var successRow:Int=0
    var score=0.0
    var data:QuestionTrivia?=null
    var answer=" "
    var allVariants= mutableListOf<String>(" "," "," "," ")
    fun isRight(string: String):Boolean
    {
        return if (string==answer) {
            successRow+=1
            score+1
            true
        } else {
            successRow=0
            false
        }
    }
    fun getHint():Any
    {

        return if (successRow>=3) {
            var a=(0..3).random()
            while (allVariants[a]==answer)
            {
                a=(0..3).random()
            }
            allVariants[a]=" "
            successRow=0
            return allVariants
        } else {
            "У вас нет необходимой серии побед для подсказки"
        }
    }

}