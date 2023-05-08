package com.example.trivia.game

class MillyProc {
    var score=1
    var question=""
    var answer=""
    val listOfAnswers= mutableListOf<String>()
    fun isCorrect(string: String):Boolean
    {
        score+=1
        return string.lowercase().trim(' ')==answer.lowercase().trim(' ')
    }
    fun getHint()
    {
        val randomIndex = (0 until listOfAnswers.size).random()
        while (randomIndex!=listOfAnswers.indexOf(answer))
        {
            if (listOfAnswers[randomIndex] != answer) {
                listOfAnswers[randomIndex] = " "
                return
            }
        }
    }
}