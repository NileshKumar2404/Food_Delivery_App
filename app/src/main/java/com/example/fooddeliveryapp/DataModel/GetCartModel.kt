package com.example.fooddeliveryapp.DataModel

data class GetCartModelResponse(
    val statusCode: Int,
    val data: GetCartData,
    val message: String,
    val success: Boolean
)
data class GetCartData(
    val cartItems: List<CartItems>,
    val total: Int
)
data class RemoveItemFromCartModelResponse(
    val statusCode: Int,
    val data: RemoveCartData,
    val message: String,
    val success: Boolean
)
data class RemoveCartData(
    val cart: List<CartItems>,
    val total: Int
)
data class CartItems(
    val _id: String,
    val user: String,
    val menuItem: menu,
    val quantity: Int,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
data class menu(
    val _id: String,
    val name: String,
    val restaurant: String,
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