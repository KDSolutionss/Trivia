package com.example.trivia.util

import com.squareup.moshi.Json

data class TriviaJson(@Json(name="response_code")val code: String,
                      @Json(name="results")val results: List<Result>)
data class Result(@Json(name="category")val category:String,
                  @Json(name="type")val type:String,
                  @Json(name="difficulty")val difficulty:String,
                  @Json(name="question")val question:String,
                  @Json(name="correct_answer")val correct_answer:String,
                  @Json(name="incorrect_answers")val incorrect_answers:List<String>)