package com.example.fooddeliveryapp.DataModel

import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    val restaurantId: String,
    val addressId: String,
    val items: List<Items>,
    val paymentMethod: String
)
data class Items(
    val menuItem: String,
    val quantity: Int
)

data class PlaceOrderResponse(
    val statusCode: Int,
    val data: PlaceOrderData?,
    val message: String,
    val success: Boolean
)

data class PlaceOrderData(
    val order: Order
)

data class Order(
    val customer: String,
    val restaurant: String,
    val items: List<OrderItem>,
    val totalPrice: Int,
    val status: String,
    val deliveryAddress: String,
    val payment: PaymentDetails,
    @SerializedName("_id") val id: String,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val v: Int
)

data class OrderItem(
    val menuItem: String,
    val quantity: Int,
    val price: Int,
    @SerializedName("_id") val id: String
)

data class PaymentDetails(
    val method: String,
    val status: String,
)
