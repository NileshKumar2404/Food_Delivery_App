package com.example.fooddeliveryapp.DataModel

data class SearchRestaurantResponse(
    val statusCode: Int,
    val data: RestaurantsData,
    val message: String,
    val success: Boolean
)

data class RestaurantsData(
    val restaurants: List<Restaurants>
)

data class Restaurants(
    val _id: String,
    val name: String,
    val address: String,
    val cuisine: List<String>,
    val ratings: Int,
    val image: String
)