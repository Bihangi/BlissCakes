package com.blisscakes.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val id: Int,
    val cakeId: Int,
    val cakeName: String,
    val cakeImage: String?,
    val quantity: Int,
    val price: Double,
    val customization: String?,
    val updatedAt: Long = System.currentTimeMillis()
)

