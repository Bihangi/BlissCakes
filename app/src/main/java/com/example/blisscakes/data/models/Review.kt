package com.blisscakes.app.data.models

import com.google.gson.annotations.SerializedName

data class ReviewsResponse(
    val success: Boolean,
    val data: ReviewData
)

data class ReviewData(
    val reviews: List<Review>,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("total_reviews") val totalReviews: Int
)

data class Review(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("cake_id") val cakeId: Int,
    val rating: Int,
    val comment: String?,
    @SerializedName("created_at") val createdAt: String,
    val user: ReviewUser?
)

data class ReviewUser(
    val username: String,
    @SerializedName("first_name") val firstName: String
)

data class ReviewRequest(
    @SerializedName("cake_id") val cakeId: Int,
    val rating: Int,
    val comment: String?
)