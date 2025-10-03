package com.example.fooddeliveryapp.DataModel

data class AddFavouriteMenuItemResponse(
    val statusCode: Int,
    val data: FavouriteMenuItemData,
    val message: String,
    val success: Boolean
)
data class FavouriteMenuItemData(
    val addInFavourite: MenuItemDetails
)
data class MenuItemDetails(
    val _id: String,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int,
    val favouriteMenuItems: List<String>,
    val favouriteRestaurants: List<String>
)
