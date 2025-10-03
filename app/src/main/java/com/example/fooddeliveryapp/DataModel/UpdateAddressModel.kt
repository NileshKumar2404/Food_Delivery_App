package com.example.fooddeliveryapp.DataModel

data class UpdateAddressRequest(
    val name: String,
    val phone: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pinCode: Int,
)

data class UpdateAddressModelResponse(
    val statusCode: String,
    val data: UpdatedAddressData,
    val message: String,
    val success: Boolean
)
data class UpdatedAddressData(
    val address: AddressData
)
data class AddressData(
    val coordinates: Coordinates,
    val _id: String,
    val user: String,
    val name: String,
    val phone: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val isDefault: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)