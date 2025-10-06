package com.example.blisscakes.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val username: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val phone: String? = null,
    val address: String? = null
)

data class AuthResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val token: String? = null,
    val user: User? = null
)

data class User(
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val username: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    @SerializedName("user_type")
    val userType: String? = null
)