package com.example.trivia.data

import androidx.lifecycle.MutableLiveData
import com.example.trivia.database.QuestionTrivia
import com.example.trivia.util.TriviaApi

class TriviaViewModel {
    var entities = MutableLiveData<List<QuestionTrivia>>()
    private var pointer = 0
    suspend fun ReloadEntity() {
        entities.value = TriviaApi
            .retrofitService
            .getQuestions()
            .results
            .map {
                val a = it.incorrect_answers.map { result ->
applyASCIIconvertation(result)                }.toMutableList()
                a.add(it.correct_answer)
                QuestionTrivia(
                    applyASCIIconvertation(it.question)
                    ,applyASCIIconvertation(it.correct_answer),
                    a
                )
            }
    }
    fun applyASCIIconvertation(str:String):String{
        return str.replace("&quot;", "\"").replace("&#039;", "\'").replace("&amp;", "&")
    }

    suspend fun getNext(): QuestionTrivia {
        return if (pointer < 10) {
            pointer += 1
            entities.value!![pointer - 1]
        } else {
            pointer = 1
            ReloadEntity()
            entities.value!![pointer - 1]

        }


    }
}