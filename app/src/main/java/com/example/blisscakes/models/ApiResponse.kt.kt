package com.example.blisscakes.models

data class `ApiResponse.kt`<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)