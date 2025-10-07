package com.blisscakes.app.data.models

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(
    val success: Boolean,
    val data: List<Category>
)

data class Category(
    val id: Int,
    val name: String,
    val description: String?,
    val image: String?,
    @SerializedName("image_url") val imageUrl: String?
)