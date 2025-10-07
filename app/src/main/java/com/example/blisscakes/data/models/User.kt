package com.blisscakes.app.data.models

import com.google.gson.annotations.SerializedName

// Authentication Models
data class RegisterRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String,
    val phone: String?,
    val address: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val token: String?,
    val user: User?,
    val message: String?
)

data class UserResponse(
    val success: Boolean,
    val user: User
)

data class User(
    val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val username: String,
    val email: String,
    @SerializedName("user_type") val userType: String,
    val phone: String?,
    val address: String?,
    @SerializedName("two_factor_enabled") val twoFactorEnabled: Boolean
)