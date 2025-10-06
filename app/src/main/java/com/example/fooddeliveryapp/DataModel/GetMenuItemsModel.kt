package com.example.fooddeliveryapp.DataModel

data class GetMenuItemsModelResponse(
    val statusCode: Int,
    val data: MenuItemWrapper,
    val message: String,
    val success: Boolean
)
data class MenuItemWrapper(
    val menuItem: MenuItemDataContainer
)
data class MenuItemDataContainer(
    val _id: String,
    val name: String,
    val restaurant: RestaurantDetails,
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

data class RestaurantDetails(
    val _id: String,
    val name: String,
    val owner: String,
    val address: String,
    val description: String,
    val cuisine: List<String>,
    val ratings: Double,
    val image: String,
    val menu: List<String>,
    val isOpen: Boolean,
    val featured: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)