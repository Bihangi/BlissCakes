package com.blisscakes.app.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.Order
import com.blisscakes.app.data.repository.OrderRepository
import com.blisscakes.app.utils.Resource
import com.blisscakes.app.utils.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<ViewState<List<Order>>>(ViewState.Idle)
    val orders: StateFlow<ViewState<List<Order>>> = _orders.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _orders.value = ViewState.Loading

            orderRepository.getOrders(forceRefresh).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _orders.value = ViewState.Success(resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _orders.value = ViewState.Error(resource.message ?: "Failed to load orders")
                    }
                    is Resource.Loading -> {
                        _orders.value = ViewState.Loading
                    }
                }
            }
        }
    }

    fun getOrdersByStatus(status: String): List<Order> {
        return when (val currentState = _orders.value) {
            is ViewState.Success -> currentState.data.filter { it.status == status }
            else -> emptyList()
        }
    }
}