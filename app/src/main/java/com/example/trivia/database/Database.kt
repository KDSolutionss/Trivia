package com.example.trivia.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors


@Database(entities = [QuestionEntity::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase: RoomDatabase() {
    abstract fun QuestionsDAO(): QDAO

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null
        fun getInstance(context: Context): ItemRoomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        private fun buildDatabase(context: Context)=
                Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "try_next_Upp"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val questions= mutableListOf<QuestionEntity>()
                        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO)  {
                            context.assets.open("questions.txt").bufferedReader().forEachLine { line ->
                                val parts = line.split("*")
                                val title = parts[0]
                                val author = parts[1]
                                questions.add(QuestionEntity(question = title, answer = author))
                            }
                            getInstance(context)
                                .QuestionsDAO()
                                .insertBooks(questions)

                        }
                    }
                })
                    .fallbackToDestructiveMigration()
                    .build()
    }
}

