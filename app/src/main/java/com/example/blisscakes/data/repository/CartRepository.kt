package com.blisscakes.app.data.repository

import com.blisscakes.app.data.local.dao.CartDao
import com.blisscakes.app.data.local.entities.CartEntity
import com.blisscakes.app.data.models.*
import com.blisscakes.app.data.remote.ApiService
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first

class CartRepository(
    private val apiService: ApiService,
    private val cartDao: CartDao
) {

    fun getCart(): Flow<Resource<CartData>> = flow {
        emit(Resource.Loading())

        // Load from local cache first
        val localItems = cartDao.getAllCartItems().first()
        if (localItems.isNotEmpty()) {
            val cartData = CartData(
                items = localItems.map { it.toCartItem() },
                totalAmount = localItems.sumOf { it.quantity * it.price },
                totalItems = localItems.sumOf { it.quantity }
            )
            emit(Resource.Success(cartData))
        }

        // Sync with server
        try {
            val response = apiService.getCart()
            if (response.isSuccessful && response.body()?.success == true) {
                val serverCart = response.body()!!.data

                // Update local cache
                cartDao.clearCart()
                serverCart.items.forEach { item ->
                    cartDao.insertCartItem(item.toEntity())
                }

                emit(Resource.Success(serverCart))
            } else {
                // If server fails, use local cache
                if (localItems.isEmpty()) {
                    emit(Resource.Error("Cart is empty"))
                }
            }
        } catch (e: Exception) {
            // Network error - use local cache
            if (localItems.isEmpty()) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    suspend fun addToCart(
        cakeId: Int,
        quantity: Int,
        customization: Map<String, String>? = null
    ): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())

        try {
            val request = AddToCartRequest(cakeId, quantity, customization)
            val response = apiService.addToCart(request)

            if (response.isSuccessful && response.body()?.success == true) {
                // Refresh cart after adding
                getCart().collect { }
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to add to cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun updateCartItem(id: Int, quantity: Int): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())

        try {
            val request = UpdateCartRequest(quantity)
            val response = apiService.updateCartItem(id, request)

            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to update cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun removeFromCart(id: Int): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.removeFromCart(id)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to remove item"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun clearCart(): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.clearCart()
            cartDao.clearCart()

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Failed to clear cart"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()

    fun getCartTotal(): Flow<Double> = flow {
        cartDao.getCartTotal().collect { total ->
            emit(total ?: 0.0)
        }
    }

    // Conversion extensions
    private fun CartEntity.toCartItem() = CartItem(
        id = id,
        cakeId = cakeId,
        cake = Cake(
            id = cakeId,
            name = cakeName,
            description = "",
            price = price,
            image = cakeImage,
            categoryId = 0,
            category = null,
            size = "",
            flavor = "",
            occasion = "",
            dietaryOptions = emptyList(),
            isAvailable = true,
            averageRating = 0.0,
            totalReviews = 0,
            imageUrl = cakeImage,
            formattedPrice = null
        ),
        quantity = quantity,
        price = price,
        subtotal = quantity * price,
        customization = null
    )

    private fun CartItem.toEntity() = CartEntity(
        id = id,
        cakeId = cakeId,
        cakeName = cake.name,
        cakeImage = cake.image,
        quantity = quantity,
        price = price,
        customization = customization?.toString()
    )
}
