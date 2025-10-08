package com.example.fooddeliveryapp.DataModel

data class GetAllMenuItemsModelResponse(
    val statusCode: Int,
    val data: List<MenuItemsContainer>,
    val message: String,
    val success: Boolean
)
data class MenuItemsContainer(
    val _id: String,
    val name: String,
    val restaurant: RestaurantData,
    val description: String,
    val price: String,
    val category: String,
    val image: String,
    val isAvailable: Boolean,
    val ratings: Double,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
data class RestaurantData(
    val _id: String,
    val name: String,
)