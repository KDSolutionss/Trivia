package com.example.trivia.util

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface TriviaApiService {
    @GET("api.php?amount=10&type=multiple&difficulty=easy")
   suspend fun getQuestions():TriviaJson
}
object TriviaApi {
    private const val BASE_URL = "https://opentdb.com/"
    val retrofitService : TriviaApiService by lazy {
        Retrofit
            .Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build().create(TriviaApiService::class.java)
    }
}