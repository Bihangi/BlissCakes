package com.example.blisscakes.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cakes")
data class Cake(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String? = null,
    @SerializedName("category_id")
    val categoryId: Int,
    val flavor: String? = null,
    val size: String? = null,
    val occasion: String? = null,
    @SerializedName("is_available")
    val isAvailable: Boolean = true,
    val ingredients: String? = null,
    @SerializedName("dietary_options")
    val dietaryOptions: String? = null,
    @SerializedName("average_rating")
    val averageRating: Double = 0.0,
    @SerializedName("total_reviews")
    val totalReviews: Int = 0,
    val category: Category? = null
) : Parcelable

@Parcelize
data class Category(
    val id: Int,
    val name: String,
    val description: String? = null,
    val image: String? = null
) : Parcelable

data class CakesResponse(
    val success: Boolean? = null,
    val data: List<Cake>? = null,
    val cakes: List<Cake>? = null,
    val message: String? = null
)