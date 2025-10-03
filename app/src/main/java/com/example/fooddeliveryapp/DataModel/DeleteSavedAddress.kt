package com.example.fooddeliveryapp.DataModel

data class DeleteSavedAddressResponse(
    val statusCode: Int,
    val data: Any,
    val message: String,
    val success: Boolean
)