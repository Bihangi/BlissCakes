package com.example.blisscakes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blisscakes.DataClasses.CakeRepository
import com.example.blisscakes.DataClasses.Resource
import com.example.blisscakes.models.Cake
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CakesState(
    val isLoading: Boolean = false,
    val cakes: List<Cake> = emptyList(),
    val error: String? = null,
    val selectedCategory: Int? = null
)

@HiltViewModel
class CakeViewModel @Inject constructor(
    private val cakeRepository: CakeRepository
) : ViewModel() {

    private val _cakesState = MutableStateFlow(CakesState())
    val cakesState: StateFlow<CakesState> = _cakesState.asStateFlow()

    private val _selectedCake = MutableStateFlow<Cake?>(null)
    val selectedCake: StateFlow<Cake?> = _selectedCake.asStateFlow()

    init {
        loadCakes()
    }

    fun loadCakes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            cakeRepository.getCakes(forceRefresh).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _cakesState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _cakesState.update {
                            it.copy(
                                isLoading = false,
                                cakes = result.data ?: emptyList(),
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _cakesState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadCakeById(cakeId: Int) {
        viewModelScope.launch {
            cakeRepository.getCakeById(cakeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _selectedCake.value = result.data
                    }
                    is Resource.Error -> {
                        _cakesState.update { it.copy(error = result.message) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun filterByCategory(categoryId: Int?) {
        _cakesState.update { it.copy(selectedCategory = categoryId) }

        if (categoryId == null) {
            loadCakes()
        } else {
            viewModelScope.launch {
                cakeRepository.getCakesByCategory(categoryId).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _cakesState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _cakesState.update {
                                it.copy(
                                    isLoading = false,
                                    cakes = result.data ?: emptyList(),
                                    error = null
                                )
                            }
                        }
                        is Resource.Error -> {
                            _cakesState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun searchCakes(query: String) {
        val filteredCakes = _cakesState.value.cakes.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.flavor?.contains(query, ignoreCase = true) == true
        }
        _cakesState.update { it.copy(cakes = filteredCakes) }
    }

    fun clearError() {
        _cakesState.update { it.copy(error = null) }
    }
}