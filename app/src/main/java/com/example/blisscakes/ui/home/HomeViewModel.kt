package com.blisscakes.app.ui.home

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

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CakeRepository(application)

    private val _featuredCakes = MutableStateFlow<ViewState<List<Cake>>>(ViewState.Idle)
    val featuredCakes: StateFlow<ViewState<List<Cake>>> = _featuredCakes.asStateFlow()

    init {
        loadFeaturedCakes()
    }

    fun loadFeaturedCakes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _featuredCakes.value = ViewState.Loading
            try {
                val cakes = repository.getFeaturedCakes(forceRefresh)
                _featuredCakes.value = ViewState.Success(cakes)
            } catch (e: Exception) {
                _featuredCakes.value = ViewState.Error(
                    e.message ?: "Failed to load cakes"
                )
            }
        }
    }
}