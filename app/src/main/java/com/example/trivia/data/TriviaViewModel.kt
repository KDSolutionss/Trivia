package com.example.trivia.data

import androidx.lifecycle.MutableLiveData
import com.example.trivia.database.QuestionTrivia
import com.example.trivia.util.TriviaApi

class TriviaViewModel {
    var _entityList = MutableLiveData<List<QuestionTrivia>>()
    private var pointer=0
    suspend fun ReloadEntity()
    {
        _entityList.value=TriviaApi
            .retrofitService
            .getQuestions()
            .results
            .map {
                val a=it.incorrect_answers.map { it.replace("&quot;","\"").replace("&#039;","\'") }.toMutableList()
                a.add(it.correct_answer)
                QuestionTrivia(it.question.replace("&quot;","\"").replace("&#039;","\'"),
                    it.correct_answer.replace("&quot;","\"").replace("&#039;","\'"),
                    a
                )}
    }
    suspend fun getNext():QuestionTrivia
    {
        return if (pointer<10) {
            pointer+=1
            _entityList.value!![pointer-1]
        } else {
            pointer=1
            ReloadEntity()
            _entityList.value!![pointer-1]

        }


    }
}