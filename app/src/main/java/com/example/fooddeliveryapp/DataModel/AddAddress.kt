package com.example.fooddeliveryapp.DataModel

data class AddAddressRequest(
    val name: String,
    val phone: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pinCode: Int,
    val coordinates: Coordinates ?= null
)

data class Coordinates(
    val lat: Double? = null,
    val long: Double? = null
)

data class AddAddressResponse(
    val statusCode: Int,
    val data: createAddress,
    val message: String,
    val success: Boolean
)

data class createAddress(
    val user: String,
    val name: String,
    val phone: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val coordinates: List<String>,
    val isDefault: Boolean,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)