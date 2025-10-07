package com.blisscakes.app.data.models

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
    val success: Boolean,
    val data: List<Order>
)

data class OrderResponse(
    val success: Boolean,
    val data: Order
)

data class Order(
    val id: Int,
    @SerializedName("order_number") val orderNumber: String,
    val status: String,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("delivery_phone") val deliveryPhone: String,
    @SerializedName("delivery_date") val deliveryDate: String?,
    val items: List<OrderItem>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("formatted_total") val formattedTotal: String?,
    @SerializedName("status_color") val statusColor: String?
)

data class OrderItem(
    val id: Int,
    @SerializedName("cake_id") val cakeId: Int,
    val cake: Cake,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

data class PlaceOrderRequest(
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("delivery_phone") val deliveryPhone: String,
    @SerializedName("delivery_date") val deliveryDate: String?,
    @SerializedName("payment_method") val paymentMethod: String = "cash"
)