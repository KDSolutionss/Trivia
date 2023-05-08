package com.example.trivia.data

import androidx.lifecycle.MutableLiveData
import com.example.trivia.database.QuestionTrivia
import com.example.trivia.util.TriviaApi

class TriviaViewModel {
    var _entityList = MutableLiveData<List<QuestionTrivia>>()
    lateinit var currentEntity:QuestionTrivia
    private var pointer=0
    suspend fun ReloadEntity()
    {
        _entityList.value=TriviaApi
            .retrofitService
            .getQuestions()
            .results
            .map {
                val a=it.incorrect_answers.toMutableList()
                a.add(it.correct_answer)
                QuestionTrivia(it.question.replace("&quot;","\""),
                    it.correct_answer.replace("&quot;","\""),
                    a
                )}
    }
    fun getNext():Pair<QuestionTrivia,Int>
    {
        while (pointer<10)
        {
            pointer+=1
            return Pair(_entityList.value!![pointer-1],pointer)

        }
        return Pair(_entityList.value!![pointer-1],pointer)

    }
}