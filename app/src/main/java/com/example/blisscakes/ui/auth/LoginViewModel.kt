package com.blisscakes.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.AuthResponse
import com.blisscakes.app.data.repository.AuthRepository
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<AuthResponse>>()
    val loginState: LiveData<Resource<AuthResponse>> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { resource ->
                _loginState.value = resource
            }
        }
    }

    fun validateInput(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}