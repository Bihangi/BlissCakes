package com.example.blisscakes.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("cake_id")
    val cakeId: Int,
    @SerializedName("cake_name")
    val cakeName: String,
    val price: Double,
    val image: String? = null,
    val quantity: Int = 1,
    val customization: String? = null
)

data class Cart(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("cart_items")
    val cartItems: List<CartItemResponse>,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("total_items")
    val totalItems: Int
)

data class CartItemResponse(
    val id: Int,
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("cake_id")
    val cakeId: Int,
    val quantity: Int,
    val price: Double,
    val customization: String? = null,
    val cake: Cake? = null
)

data class AddToCartRequest(
    @SerializedName("cake_id")
    val cakeId: Int,
    val quantity: Int = 1,
    val price: Double,
    val customization: String? = null
)

data class UpdateCartRequest(
    val quantity: Int,
    val customization: String? = null
)