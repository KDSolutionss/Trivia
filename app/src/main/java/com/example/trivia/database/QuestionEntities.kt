package com.example.trivia.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(@PrimaryKey val question:String,val answer:String)


data class QuestionFirebase(val answer:String?="",val question:String?="")
data class QuestionTrivia(val question:String, val answer: String, var incorrectAnswers:List<String>)