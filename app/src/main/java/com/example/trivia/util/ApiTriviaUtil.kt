package com.example.trivia.util

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface TriviaApiService {
    @GET("api.php?amount=10&type=multiple")
   suspend fun getQuestions():TriviaJson
}
object TriviaApi {
    val retrofitService : TriviaApiService by lazy {
        Retrofit
            .Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("https://opentdb.com/")
            .build().create(TriviaApiService::class.java)
    }
}