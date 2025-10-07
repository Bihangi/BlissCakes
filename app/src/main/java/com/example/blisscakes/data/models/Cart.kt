package com.blisscakes.app.data.models

import com.google.gson.annotations.SerializedName

data class CartResponse(
    val success: Boolean,
    val data: CartData
)

data class CartData(
    val items: List<CartItem>,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("total_items") val totalItems: Int
)

data class CartItem(
    val id: Int,
    @SerializedName("cake_id") val cakeId: Int,
    val cake: Cake,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val customization: Map<String, String>?
)

data class AddToCartRequest(
    @SerializedName("cake_id") val cakeId: Int,
    val quantity: Int,
    val customization: Map<String, String>?
)

data class UpdateCartRequest(
    val quantity: Int
)