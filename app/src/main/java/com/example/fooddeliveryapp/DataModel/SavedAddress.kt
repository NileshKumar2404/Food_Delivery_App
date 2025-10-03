package com.example.fooddeliveryapp.DataModel

data class SavedAddressResponse(
    val statusCode: Int,
    val data: List<SavedAddress>,
    val message: String,
    val success: Boolean
)
data class SavedAddress(
    val _id: String,
    val name: String,
    val phone: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pinCode: String,
    val coordinates: AddressCoordinates? = null,
)
data class AddressCoordinates(
    val lat: Double? = null,
    val long: Double? = null
)