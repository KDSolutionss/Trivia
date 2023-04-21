package com.example.trivia.data


import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.*
import com.example.trivia.database.ItemRoomDatabase
import com.example.trivia.database.QuestionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MyViewModel(app: Application) : ViewModel() {
    private val userDao = ItemRoomDatabase.getInstance(app).QuestionsDAO()
    lateinit var quote:QuestionEntity
    fun process()
    {
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            quote=userDao.getQuestion()
        }
    }


}