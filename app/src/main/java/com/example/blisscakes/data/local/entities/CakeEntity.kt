package com.blisscakes.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "cakes")
data class CakeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String?,
    val categoryId: Int,
    val categoryName: String,
    val size: String,
    val flavor: String,
    val occasion: String,
    val dietaryOptions: List<String>,
    val isAvailable: Boolean,
    val averageRating: Double,
    val totalReviews: Int,
    val cachedAt: Long = System.currentTimeMillis()
)

