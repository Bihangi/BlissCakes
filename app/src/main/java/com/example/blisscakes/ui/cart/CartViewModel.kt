package com.blisscakes.app.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.CartData
import com.blisscakes.app.data.repository.CartRepository
import com.blisscakes.app.utils.Resource
import com.blisscakes.app.utils.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cart = MutableStateFlow<ViewState<CartData>>(ViewState.Idle)
    val cart: StateFlow<ViewState<CartData>> = _cart.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private val _cartTotal = MutableStateFlow(0.0)
    val cartTotal: StateFlow<Double> = _cartTotal.asStateFlow()

    init {
        loadCart()
        observeCartCount()
        observeCartTotal()
    }

    fun loadCart(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _cart.value = ViewState.Loading

            cartRepository.getCart().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _cart.value = ViewState.Success(resource.data!!)
                    }
                    is Resource.Error -> {
                        _cart.value = ViewState.Error(resource.message ?: "Failed to load cart")
                    }
                    is Resource.Loading -> {
                        _cart.value = ViewState.Loading
                    }
                }
            }
        }
    }

    fun updateQuantity(itemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateCartItem(itemId, newQuantity).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadCart(forceRefresh = true)
                    }
                    is Resource.Error -> {
                        // Handle error
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            cartRepository.removeFromCart(itemId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadCart(forceRefresh = true)
                    }
                    is Resource.Error -> {
                        // Handle error
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        loadCart(forceRefresh = true)
                    }
                    is Resource.Error -> {
                        // Handle error
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _cartItemCount.value = count
            }
        }
    }

    private fun observeCartTotal() {
        viewModelScope.launch {
            cartRepository.getCartTotal().collect { total ->
                _cartTotal.value = total
            }
        }
    }
}
