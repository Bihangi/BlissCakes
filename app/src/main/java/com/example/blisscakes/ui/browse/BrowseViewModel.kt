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

class BrowseViewModel(application: Application) : AndroidViewModel(application) {

    private val cakeRepository = CakeRepository(application)

    private val _cakes = MutableStateFlow<ViewState<List<Cake>>>(ViewState.Idle)
    val cakes: StateFlow<ViewState<List<Cake>>> = _cakes.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Int?>(null)
    val selectedCategory: StateFlow<Int?> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadAllCakes()
    }

    fun loadAllCakes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _cakes.value = ViewState.Loading
            try {
                val categoryId = _selectedCategory.value
                val cakesList = cakeRepository.getAllCakes(categoryId)
                _cakes.value = ViewState.Success(cakesList)
            } catch (e: Exception) {
                _cakes.value = ViewState.Error(e.message ?: "Failed to load cakes")
            }
        }
    }

    fun searchCakes(query: String) {
        _searchQuery.value = query

        if (query.isBlank()) {
            loadAllCakes()
            return
        }

        viewModelScope.launch {
            _cakes.value = ViewState.Loading
            try {
                // Filter cakes locally by name
                val allCakes = cakeRepository.getAllCakes(_selectedCategory.value)
                val filteredCakes = allCakes.filter { cake ->
                    cake.name.contains(query, ignoreCase = true) ||
                            cake.description.contains(query, ignoreCase = true)
                }
                _cakes.value = ViewState.Success(filteredCakes)
            } catch (e: Exception) {
                _cakes.value = ViewState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun filterByCategory(categoryId: Int?) {
        _selectedCategory.value = categoryId
        loadAllCakes()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        loadAllCakes()
    }
}