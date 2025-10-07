package com.blisscakes.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Int,
    val orderNumber: String,
    val status: String,
    val paymentStatus: String,
    val totalAmount: Double,
    val deliveryAddress: String,
    val deliveryPhone: String,
    val deliveryDate: String?,
    val items: String, // JSON string
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)

