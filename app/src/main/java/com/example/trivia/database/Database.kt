package com.example.trivia.database

import android.content.Context

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [QuestionEntity::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {
    abstract fun questionsDAO(): QDAO
    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null
        fun getInstance(context: Context): ItemRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ItemRoomDatabase::class.java,
                        "newY"
                    ).addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val questions = mutableListOf<QuestionEntity>()
                            CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                                context.assets.open("questions.txt").bufferedReader()
                                    .forEachLine { line ->
                                        val parts = line.split("*")
                                        val title = parts[0]
                                        val author = parts[1]
                                        questions.add(
                                            QuestionEntity(
                                                question = title,
                                                answer = author
                                            )
                                        )
                                    }
                                getInstance(context)
                                    .questionsDAO()
                                    .insertBooks(questions)
                                getInstance(context).mIsDatabaseCreated.postValue(true)
                            }
                        }
                    })
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }


    }

    fun getDatabaseCreated(): LiveData<Boolean> {
        return mIsDatabaseCreated
    }
}


