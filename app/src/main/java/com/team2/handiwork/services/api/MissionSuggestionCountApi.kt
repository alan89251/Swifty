package com.team2.handiwork.services.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MissionSuggestionCountApi {
    @GET("api/suggestionCount/{userID}")
    suspend fun getSuggestionCount(@Path("userID") key: String): Response<SuggestCountResponseModel>
}