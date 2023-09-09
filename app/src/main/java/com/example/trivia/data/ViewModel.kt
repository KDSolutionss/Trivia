package com.example.trivia.data


import android.app.Application
import androidx.lifecycle.*
import com.example.trivia.database.ItemRoomDatabase
import com.example.trivia.database.QuestionEntity
import kotlinx.coroutines.*

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
    var db = ItemRoomDatabase.getInstance(app)
    private val userDao = db.questionsDAO()
    lateinit var quote: QuestionEntity
    private val isDatabaseCreated = MediatorLiveData<Boolean>()

    init {
        isDatabaseCreated.addSource(db.getDatabaseCreated()) { isCreated ->
            isDatabaseCreated.value = isCreated
        }
    }

    fun getIsDatabaseCreated(): LiveData<Boolean> {
        return isDatabaseCreated
    }

    fun process() {
        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            quote = userDao.getQuestion().value!!
        }
    }


}