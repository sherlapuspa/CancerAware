package com.dicoding.asclepius.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "https://newsapi.org/"

    private val retro =
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiInst = retro.create(ApiService::class.java)
}