package com.example.fooddeliveryapp.DataModel

data class GetListOfFavouritesResponse(
    val statusCode: Int,
    val data: FavouriteData,
    val message: String,
    val success: Boolean
)
data class FavouriteData(
    val favouriteRestaurants: List<FavouriteRestaurant>,
    val favouriteMenuItems: List<FavouriteMenuItem>
)
data class FavouriteRestaurant(
    val _id: String,
    val name: String,
    val owner: String,
    val address: favouriteRestaurantAddress,
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
data class FavouriteMenuItem(
    val _id: String,
    val name: String,
    val restaurant: RestaurantValue,
    val description: String,
    val price: String,
    val image: String,
    val isAvailable: Boolean,
    val ratings: Double,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)
data class RestaurantValue(
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
data class favouriteRestaurantAddress(
    val _id: String,
    val street: String,
    val city: String
)