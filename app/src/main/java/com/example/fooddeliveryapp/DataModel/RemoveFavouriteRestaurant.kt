package com.example.fooddeliveryapp.DataModel

data class RemoveFavouriteRestaurantResponse(
    val statusCode: Int,
    val data: Restaurant,
    val message: String,
    val success: Boolean
)

data class Restaurant(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val favouriteMenuItem: List<String>,
    val favouriteRestaurants: List<String>
)
