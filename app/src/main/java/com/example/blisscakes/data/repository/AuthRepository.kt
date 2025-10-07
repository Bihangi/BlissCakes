package com.blisscakes.app.data.repository

import com.blisscakes.app.data.models.*
import com.blisscakes.app.data.preferences.UserPreferences
import com.blisscakes.app.data.remote.ApiService
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String,
        phone: String?,
        address: String?
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val request = RegisterRequest(
                firstName, lastName, username, email,
                password, passwordConfirmation, phone, address
            )
            val response = apiService.register(request)

            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                authResponse.token?.let { userPreferences.saveAuthToken(it) }
                authResponse.user?.let {
                    userPreferences.saveUserData(
                        it.id,
                        it.email,
                        "${it.firstName} ${it.lastName}"
                    )
                }
                emit(Resource.Success(authResponse))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun login(email: String, password: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!
                authResponse.token?.let { userPreferences.saveAuthToken(it) }
                authResponse.user?.let {
                    userPreferences.saveUserData(
                        it.id,
                        it.email,
                        "${it.firstName} ${it.lastName}"
                    )
                }
                emit(Resource.Success(authResponse))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun logout(): Flow<Resource<MessageResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.logout()
            userPreferences.clearUserData()

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Logout failed"))
            }
        } catch (e: Exception) {
            // Clear local data even if API call fails
            userPreferences.clearUserData()
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun getCurrentUser(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getUser()

            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(response.body()!!.user))
            } else {
                emit(Resource.Error("Failed to fetch user data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }
}
