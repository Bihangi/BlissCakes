package com.blisscakes.app.ui.browse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.Cake
import com.blisscakes.app.data.repository.CakeRepository
import com.blisscakes.app.utils.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CakeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val cakeRepository = CakeRepository(application)

    private val _cake = MutableStateFlow<ViewState<Cake>>(ViewState.Idle)
    val cake: StateFlow<ViewState<Cake>> = _cake.asStateFlow()

    private val _addToCartState = MutableStateFlow<ViewState<Boolean>>(ViewState.Idle)
    val addToCartState: StateFlow<ViewState<Boolean>> = _addToCartState.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun loadCakeDetails(cakeId: Int) {
        viewModelScope.launch {
            _cake.value = ViewState.Loading
            try {
                val cake = cakeRepository.getCakeById(cakeId)
                if (cake != null) {
                    _cake.value = ViewState.Success(cake)
                } else {
                    _cake.value = ViewState.Error("Cake not found")
                }
            } catch (e: Exception) {
                _cake.value = ViewState.Error(e.message ?: "Failed to load cake details")
            }
        }
    }

    fun incrementQuantity() {
        if (_quantity.value < 10) {
            _quantity.value++
        }
    }

    fun decrementQuantity() {
        if (_quantity.value > 1) {
            _quantity.value--
        }
    }

    fun addToCart(cakeId: Int) {
        viewModelScope.launch {
            _addToCartState.value = ViewState.Loading
            try {
                // TODO: Implement cart functionality when CartRepository is ready
                // For now, simulate success
                _addToCartState.value = ViewState.Success(true)
            } catch (e: Exception) {
                _addToCartState.value = ViewState.Error(e.message ?: "Failed to add to cart")
            }
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = ViewState.Idle
    }
}