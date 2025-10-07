package com.blisscakes.app.data.repository

import com.blisscakes.app.data.local.dao.OrderDao
import com.blisscakes.app.data.local.entities.OrderEntity
import com.blisscakes.app.data.models.*
import com.blisscakes.app.data.remote.ApiService
import com.blisscakes.app.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first

class OrderRepository(
    private val apiService: ApiService,
    private val orderDao: OrderDao
) {

    fun getOrders(forceRefresh: Boolean = false): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())

        // Load from cache first
        if (!forceRefresh) {
            val cachedOrders = orderDao.getAllOrders().first()
            if (cachedOrders.isNotEmpty()) {
                emit(Resource.Success(cachedOrders.map { it.toOrder() }))
            }
        }

        // Fetch from API
        try {
            val response = apiService.getOrders()
            if (response.isSuccessful && response.body()?.success == true) {
                val orders = response.body()!!.data

                // Cache results
                orderDao.deleteAll()
                orderDao.insertOrders(orders.map { it.toEntity() })

                emit(Resource.Success(orders))
            } else {
                val cachedOrders = orderDao.getAllOrders().first()
                if (cachedOrders.isNotEmpty()) {
                    emit(Resource.Success(cachedOrders.map { it.toOrder() }))
                } else {
                    emit(Resource.Error("Failed to fetch orders"))
                }
            }
        } catch (e: Exception) {
            val cachedOrders = orderDao.getAllOrders().first()
            if (cachedOrders.isNotEmpty()) {
                emit(Resource.Success(cachedOrders.map { it.toOrder() }))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    fun getOrderById(id: Int): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())

        // Try cache first
        val cachedOrder = orderDao.getOrderById(id).first()
        if (cachedOrder != null) {
            emit(Resource.Success(cachedOrder.toOrder()))
        }

        // Fetch from API
        try {
            val response = apiService.getOrderById(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data
                orderDao.insertOrder(order.toEntity())
                emit(Resource.Success(order))
            } else {
                if (cachedOrder == null) {
                    emit(Resource.Error("Order not found"))
                }
            }
        } catch (e: Exception) {
            if (cachedOrder == null) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    suspend fun placeOrder(
        deliveryAddress: String,
        deliveryPhone: String,
        deliveryDate: String?
    ): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())

        try {
            val request = PlaceOrderRequest(deliveryAddress, deliveryPhone, deliveryDate)
            val response = apiService.placeOrder(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data
                orderDao.insertOrder(order.toEntity())
                emit(Resource.Success(order))
            } else {
                emit(Resource.Error("Failed to place order"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    // Conversion extensions
    private fun Order.toEntity() = OrderEntity(
        id = id,
        orderNumber = orderNumber,
        status = status,
        paymentStatus = paymentStatus,
        totalAmount = totalAmount,
        deliveryAddress = deliveryAddress,
        deliveryPhone = deliveryPhone,
        deliveryDate = deliveryDate,
        items = Gson().toJson(items),
        createdAt = createdAt
    )

    private fun OrderEntity.toOrder() = Order(
        id = id,
        orderNumber = orderNumber,
        status = status,
        paymentStatus = paymentStatus,
        totalAmount = totalAmount,
        deliveryAddress = deliveryAddress,
        deliveryPhone = deliveryPhone,
        deliveryDate = deliveryDate,
        items = emptyList(), // Parse from JSON if needed
        createdAt = createdAt,
        formattedTotal = "Rs. ${String.format("%.2f", totalAmount)}",
        statusColor = null
    )
}