package com.example.fooddeliveryapp.DataModel

data class FeaturedRestaurantResponse(
    val statusCode: Int,
    val data: featuredRestaurants,
    val message: String,
    val success: Boolean
)

data class featuredRestaurants(
    val featuredRestaurants: List<featuredRestaurantsList>,
)

data class featuredRestaurantsList(
    val _id: String,
    val name: String,
    val address: String,
    val cuisine: List<String>,
    val ratings: Int,
    val image: String
//    val menu: List<FeaturedMenu>
)

//data class FeaturedMenu(
//    val _id: String,
//    val name: String,
//    val description: String,
//    val price: Int,
//    val image: String,
//    val ratings: Int
//)