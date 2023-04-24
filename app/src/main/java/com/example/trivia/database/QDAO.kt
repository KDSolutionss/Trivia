package com.example.trivia.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface QDAO {
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT 1")
    fun getQuestion():LiveData<QuestionEntity>
    @Query("SELECT * FROM questions")
    fun getQuestions(): LiveData<List<QuestionEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertBooks(books: List<QuestionEntity>)
}