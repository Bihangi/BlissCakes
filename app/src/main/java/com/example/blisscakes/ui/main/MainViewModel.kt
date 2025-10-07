package com.blisscakes.app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.repository.CartRepository
import com.blisscakes.app.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val cartRepository: CartRepository,
    private val networkUtils: NetworkUtils,
    private val sensorManager: DeviceSensorManager
) : ViewModel() {

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _availableSensors = MutableStateFlow<List<String>>(emptyList())
    val availableSensors: StateFlow<List<String>> = _availableSensors.asStateFlow()

    init {
        observeCartCount()
        observeNetworkStatus()
        checkAvailableSensors()
    }

    private fun observeCartCount() {
        viewModelScope.launch {
            cartRepository.getCartItemCount().collect { count ->
                _cartItemCount.value = count
            }
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkUtils.observeNetworkStatus().collect { isConnected ->
                _isConnected.value = isConnected
            }
        }
    }

    private fun checkAvailableSensors() {
        _availableSensors.value = sensorManager.getAvailableSensors()
    }
}