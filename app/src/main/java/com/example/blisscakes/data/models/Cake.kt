package com.blisscakes.app.data.models

import com.google.gson.annotations.SerializedName

data class CakesResponse(
    val success: Boolean,
    val data: List<Cake>
)

data class Cake(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String?,
    @SerializedName("category_id") val categoryId: Int,
    val category: Category?,
    val size: String,
    val flavor: String,
    val occasion: String,
    @SerializedName("dietary_options") val dietaryOptions: List<String>,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("total_reviews") val totalReviews: Int,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("formatted_price") val formattedPrice: String?
)