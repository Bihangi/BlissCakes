package com.blisscakes.app.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.Cake
import com.blisscakes.app.data.repository.CakeRepository
import com.blisscakes.app.utils.Resource
import com.blisscakes.app.utils.ViewState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val cakeRepository = CakeRepository(application.applicationContext)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<ViewState<List<Cake>>>(ViewState.Idle)
    val searchResults: StateFlow<ViewState<List<Cake>>> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = ViewState.Idle
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _searchResults.value = ViewState.Loading

            cakeRepository.searchCakes(query).collect { resource ->
                _searchResults.value = when (resource) {
                    is Resource.Success -> ViewState.Success(resource.data ?: emptyList())
                    is Resource.Error -> ViewState.Error(resource.message ?: "Search failed")
                    is Resource.Loading -> ViewState.Loading
                }
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = ViewState.Idle
    }
}