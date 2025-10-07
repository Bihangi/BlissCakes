package com.blisscakes.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.User
import com.blisscakes.app.data.repository.AuthRepository
import com.blisscakes.app.utils.Resource
import com.blisscakes.app.utils.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<ViewState<User>>(ViewState.Idle)
    val user: StateFlow<ViewState<User>> = _user.asStateFlow()

    private val _logoutState = MutableStateFlow<ViewState<Boolean>>(ViewState.Idle)
    val logoutState: StateFlow<ViewState<Boolean>> = _logoutState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _user.value = ViewState.Loading

            authRepository.getCurrentUser().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _user.value = ViewState.Success(resource.data!!)
                    }
                    is Resource.Error -> {
                        _user.value = ViewState.Error(resource.message ?: "Failed to load profile")
                    }
                    is Resource.Loading -> {
                        _user.value = ViewState.Loading
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = ViewState.Loading

            authRepository.logout().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _logoutState.value = ViewState.Success(true)
                    }
                    is Resource.Error -> {
                        _logoutState.value = ViewState.Error(resource.message ?: "Logout failed")
                    }
                    is Resource.Loading -> {
                        _logoutState.value = ViewState.Loading
                    }
                }
            }
        }
    }
}