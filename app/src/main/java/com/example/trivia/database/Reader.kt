package com.example.trivia.database

import java.io.File

class Reader {
    fun readTxtFile(path: String): List<QuestionEntity> {
        val questions = mutableListOf<QuestionEntity>()

        File(path).bufferedReader().forEachLine { line ->
            val parts = line.split("*")
            val title = parts[0]
            val author = parts[1]
            questions.add(QuestionEntity(question = title, answer = author))
        }
        return questions
    }
}