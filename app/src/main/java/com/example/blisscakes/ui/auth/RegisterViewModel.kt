package com.blisscakes.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blisscakes.app.data.models.AuthResponse
import com.blisscakes.app.data.repository.AuthRepository
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableLiveData<Resource<AuthResponse>>()
    val registerState: LiveData<Resource<AuthResponse>> = _registerState

    fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phone: String?,
        address: String?
    ) {
        viewModelScope.launch {
            authRepository.register(
                firstName, lastName, username, email,
                password, passwordConfirmation, phone, address
            ).collect { resource ->
                _registerState.value = resource
            }
        }
    }

    fun validateInput(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): String? {
        return when {
            firstName.isBlank() -> "First name is required"
            lastName.isBlank() -> "Last name is required"
            username.isBlank() -> "Username is required"
            username.length < 3 -> "Username must be at least 3 characters"
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            password.isBlank() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter"
            !password.matches(Regex(".*[a-z].*")) -> "Password must contain at least one lowercase letter"
            !password.matches(Regex(".*\\d.*")) -> "Password must contain at least one digit"
            password != passwordConfirmation -> "Passwords do not match"
            else -> null
        }
    }
}