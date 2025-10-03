package com.example.fooddeliveryapp.DataModel

data class SearchMenuItemResponse(
    val statusCode: Int,
    val data: MenuItemData,
    val message: String,
    val success: Boolean
)

data class MenuItemData(
    val menuItem: List<MenuItems>
)

data class MenuItems(
    val _id: String,
    val name: String,
    val image: String,
    val ratings: Int,
)