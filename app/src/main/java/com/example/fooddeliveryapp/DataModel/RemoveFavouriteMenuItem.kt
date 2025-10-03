package com.example.fooddeliveryapp.DataModel

data class RemoveFavouriteMenuItemResponse(
    val statusCode: Int,
    val data: MenuItem,
    val message: String,
    val success: Boolean
)

data class MenuItem(
    val menuItem: MenuItemContainer
)

data class MenuItemContainer(
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