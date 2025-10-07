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

class OrderDetailViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _order = MutableStateFlow<ViewState<Order>>(ViewState.Idle)
    val order: StateFlow<ViewState<Order>> = _order.asStateFlow()

    fun loadOrderDetails(orderId: Int) {
        viewModelScope.launch {
            _order.value = ViewState.Loading

            orderRepository.getOrderById(orderId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let {
                            _order.value = ViewState.Success(it)
                        } ?: run {
                            _order.value = ViewState.Error("Order not found")
                        }
                    }
                    is Resource.Error -> {
                        _order.value = ViewState.Error(resource.message ?: "Failed to load order details")
                    }
                    is Resource.Loading -> {
                        _order.value = ViewState.Loading
                    }
                }
            }
        }
    }
}