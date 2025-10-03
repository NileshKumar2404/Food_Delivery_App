package com.example.fooddeliveryapp.DataModel

data class AddtoCartRequest(
    val menuItemId: String,
    val quantity: Int
)

data class AddtoCartModelResponse(
    val statusCode: Int,
    val data: CartData,
    val message: String,
    val success: Boolean
)
data class CartData(
    val cartItem: cartItemDetails
)
data class cartItemDetails(
    val user: String,
    val menuItem: String,
    val quantity: Int,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)