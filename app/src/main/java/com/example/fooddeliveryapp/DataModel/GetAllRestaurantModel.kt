package com.example.fooddeliveryapp.DataModel

import android.view.Menu

data class GetAllRestaurantResponse(
    val statusCode: Int,
    val data: AllRestaurants,
    val message: String,
    val success: Boolean
)

data class AllRestaurants(
    val totalPages: Int,
    val totalRestaurants: Int,
    val restaurants: List<CreatedRestaurants>,
    val limit: Int,
    val page: Int
)

data class CreatedRestaurants(
    val _id: String,
    val name: String,
    val description: String,
    val cuisine: List<String>,
    val image: String,
    val menu: List<RestaurantMenu>,
    val isOpen: Boolean,
    val createdAt: String,
)

data class RestaurantMenu(
    val _id: String,
    val name: String,
    val description: String,
    val price: String,
    val image: String,
    val ratings: Number
)