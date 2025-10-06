package com.example.blisscakes.models

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("total_amount")
    val totalAmount: Double,
    val status: String,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("delivery_phone")
    val deliveryPhone: String? = null,
    @SerializedName("delivery_date")
    val deliveryDate: String? = null,
    @SerializedName("special_instructions")
    val specialInstructions: String? = null,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("order_items")
    val orderItems: List<OrderItemResponse>? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class OrderItemResponse(
    val id: Int,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("cake_id")
    val cakeId: Int,
    val quantity: Int,
    val price: Double,
    val cake: Cake? = null
)

data class CreateOrderRequest(
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("delivery_phone")
    val deliveryPhone: String,
    @SerializedName("delivery_date")
    val deliveryDate: String? = null,
    @SerializedName("special_instructions")
    val specialInstructions: String? = null
)

data class OrdersResponse(
    val success: Boolean? = null,
    val data: List<Order>? = null,
    val orders: List<Order>? = null
)

data class OrderResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val order: Order? = null
)