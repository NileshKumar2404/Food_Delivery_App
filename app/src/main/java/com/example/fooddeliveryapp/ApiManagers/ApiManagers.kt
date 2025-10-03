package com.example.fooddeliveryapp.ApiManagers

import com.example.fooddeliveryapp.Routes.ApiService
import com.example.fooddeliveryapp.URLs.RetrofitInstance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManagers {
    private val retrofit = Retrofit.Builder()
        .baseUrl(RetrofitInstance.BASE_URL) // Replace with your base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: ApiService = retrofit.create(ApiService::class.java)
}