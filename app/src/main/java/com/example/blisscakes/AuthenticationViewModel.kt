package com.example.blisscakes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blisscakes.DataClasses.AuthRepository
import com.example.blisscakes.DataClasses.Resource
import com.example.blisscakes.models.AuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val authResponse: AuthResponse? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            authRepository.getToken().collect { token ->
                _authState.update { it.copy(isLoggedIn = token != null) }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _authState.update {
                            it.copy(
                                isLoading = false,
                                authResponse = result.data,
                                isLoggedIn = true,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _authState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                                isLoggedIn = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phone: String? = null,
        address: String? = null
    ) {
        viewModelScope.launch {
            authRepository.register(
                firstName, lastName, username, email,
                password, passwordConfirmation, phone, address
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _authState.update {
                            it.copy(
                                isLoading = false,
                                authResponse = result.data,
                                isLoggedIn = true,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _authState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                                isLoggedIn = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _authState.update {
                            it.copy(
                                isLoggedIn = false,
                                authResponse = null,
                                error = null
                            )
                        }
                    }
                    else -> {
                        _authState.update { it.copy(isLoggedIn = false) }
                    }
                }
            }
        }
    }

    fun clearError() {
        _authState.update { it.copy(error = null) }
    }
}