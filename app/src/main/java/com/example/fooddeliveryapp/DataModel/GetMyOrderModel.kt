package com.example.fooddeliveryapp.DataModel

data class GetMyOrderModelResponse(
    val statusCode: Int,
    val data: MyOrders,
    val message: String,
    val success: Boolean
)
data class MyOrders(
    val orders: List<OrderData>
)
data class OrderData(
    val _id: String,
    val totalPrice: Int,
    val status: String,
    val createdAt: String,
    val payment: Payment,
    val restaurantDetails: OrderedRestaurant,
    val addressDetails: OrderedAddress,
    val menuItemDetails: List<OrderedMenuItem>
)
data class Payment(
    val method: String,
    val status: String
)
data class OrderedRestaurant(
    val _id: String,
    val name: String,
    val cuisine: List<String>,
    val ratings: Double,
    val image: String,
)
data class OrderedAddress(
    val phone: String,
    val label: String,
    val street: String,
    val city: String,
    val state: String,
    val pinCode: String,
)
data class OrderedMenuItem(
    val name: String,
    val price: String,
    val category: String,
    val image: String
)