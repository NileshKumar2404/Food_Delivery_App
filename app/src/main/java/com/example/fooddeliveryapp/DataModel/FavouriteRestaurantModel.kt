package com.example.fooddeliveryapp.DataModel

data class FavouriteRestaurantModelResponse(
    val statusCode: Int,
    val data: FavouriteRestaurantData,
    val message: String,
    val success: Boolean
)
data class FavouriteRestaurantData(
    val addInFavourite: FavouriteRestaurantAddInFavourite
)
data class FavouriteRestaurantAddInFavourite(
    val _id: String,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val favouriteMenuItem: List<String>,
    val favouriteRestaurants: List<String>
)