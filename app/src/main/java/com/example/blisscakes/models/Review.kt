package com.example.blisscakes.models

import com.google.gson.annotations.SerializedName

data class Review(
    val id: String,
    @SerializedName("cake_id")
    val cakeId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    val rating: Int,
    val comment: String,
    @SerializedName("is_verified_purchase")
    val isVerifiedPurchase: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String
)

data class ReviewsResponse(
    val success: Boolean? = null,
    val reviews: List<Review>? = null
)