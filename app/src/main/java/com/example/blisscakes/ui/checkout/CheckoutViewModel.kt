package com.blisscakes.app.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.Order
import com.blisscakes.app.data.repository.OrderRepository
import com.blisscakes.app.data.repository.CartRepository
import com.blisscakes.app.utils.Resource
import com.blisscakes.app.utils.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _placeOrderState = MutableStateFlow<ViewState<Order>>(ViewState.Idle)
    val placeOrderState: StateFlow<ViewState<Order>> = _placeOrderState.asStateFlow()

    fun placeOrder(
        deliveryAddress: String,
        deliveryPhone: String,
        deliveryDate: String?
    ) {
        viewModelScope.launch {
            _placeOrderState.value = ViewState.Loading

            orderRepository.placeOrder(deliveryAddress, deliveryPhone, deliveryDate).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Clear cart after successful order
                        cartRepository.clearCart().collect {}
                        _placeOrderState.value = ViewState.Success(resource.data!!)
                    }
                    is Resource.Error -> {
                        _placeOrderState.value = ViewState.Error(resource.message ?: "Failed to place order")
                    }
                    is Resource.Loading -> {
                        _placeOrderState.value = ViewState.Loading
                    }
                }
            }
        }
    }

    fun validateInput(
        address: String,
        phone: String
    ): String? {
        return when {
            address.isBlank() -> "Delivery address is required"
            address.length < 10 -> "Please enter a complete address"
            phone.isBlank() -> "Phone number is required"
            phone.length < 10 -> "Please enter a valid phone number"
            !phone.matches(Regex("^[0-9]{10,}$")) -> "Phone number must contain only digits"
            else -> null
        }
    }

    fun resetPlaceOrderState() {
        _placeOrderState.value = ViewState.Idle
    }
}