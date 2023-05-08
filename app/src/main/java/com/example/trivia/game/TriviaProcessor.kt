package com.example.trivia.game

import com.example.trivia.database.QuestionTrivia

class TriviaProcessor {
    private var successRow:Int=0
    var data:QuestionTrivia?=null
    var answer=" "
    fun isRight(string: String):Boolean
    {
        if (string==answer)
        {
            successRow+=1
            return true
        }
        else
        {
            successRow=0
            return false
        }
    }
    fun getHint():String
    {
        return if (successRow>=3) {
            answer.subSequence(0,2).toString()
        } else {
            "У вас нет необходимой серии побед для подсказки"
        }
    }

}