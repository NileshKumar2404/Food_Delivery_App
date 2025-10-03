package com.example.fooddeliveryapp.DataModel

data class TopRatedRestaurantResponse(
    val statusCode: Int,
    val data: topRatedRestaurants,
    val message: String,
    val success: Boolean
)

data class topRatedRestaurants(
    val topRatedRestaurants: List<listtopRatedRestaurants>
)

data class listtopRatedRestaurants(
    val _id: String,
    val name: String,
    val address: RestaurantAddress,
    val cuisine: List<String>,
    val ratings: Int,
    val image: String
)

data class RestaurantAddress(
    val _id: String,
    val street: String,
    val city: String
)