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
                val a = it.incorrect_answers.map {
                    it.replace("&quot;", "\"").replace("&#039;", "\'").replace("&amp;", "&")
                }.toMutableList()
                a.add(it.correct_answer)
                QuestionTrivia(
                    it.question.replace("&quot;", "\"").replace("&#039;", "\'")
                        .replace("&amp;", "&"),
                    it.correct_answer.replace("&quot;", "\"").replace("&#039;", "\'")
                        .replace("&amp;", "&"),
                    a
                )
            }
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