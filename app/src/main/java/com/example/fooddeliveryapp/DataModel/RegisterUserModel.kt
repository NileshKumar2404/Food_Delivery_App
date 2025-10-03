package com.example.fooddeliveryapp.DataModel

data class RegisterUserRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class RegisterUserResponse(
    val statusCode: Int,
    val data: RegisteredUser,
    val message: String,
    val success: Boolean
)

data class RegisteredUser(
    val createdUser: CreatedUser,
    val accessToken: String,
    val refreshToken: String
)

data class CreatedUser(
    val _id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)