package com.team2.handiwork.services.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {
    private const val BASE_URL = "https://mapd726-server.herokuapp.com/"
    //private const val BASE_URL = "https://10.0.2.2:8900";

    val client = OkHttpClient.Builder()
        .connectTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(100, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}