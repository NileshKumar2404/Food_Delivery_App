package com.example.fooddeliveryapp.DataModel

data class LoginUserRequest(
    val email: String,
    val password: String
)

data class LoginUserResponse(
    val statusCode: Int,
    val data: LoggedInUserData,
    val message: String,
    val success: Boolean
)

data class LoggedInUserData(
    val loggedInUser: LoggedInUser,
    val accessToken: String,
    val refreshToken: String
)

data class LoggedInUser(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)