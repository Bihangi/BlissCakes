package com.example.blisscakes.DataClasses

import android.content.Context
import com.example.blisscakes.datastore.CakeDao
import com.example.blisscakes.datastore.CartDao
import com.example.blisscakes.models.*
import com.example.blisscakes.network.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}

// Simple Token Manager
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveUserInfo(name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
            preferences[USER_EMAIL_KEY] = email
        }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    fun getUserName(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }

    fun getUserEmail(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
    }
}

@Singleton
class CakeRepository @Inject constructor(
    private val apiService: ApiService,
    private val cakeDao: CakeDao,
    @ApplicationContext private val context: Context
) {

    suspend fun getCakes(): Flow<Resource<List<Cake>>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getCakes()

            if (response.isSuccessful && response.body() != null) {
                val cakes = response.body()?.data ?: response.body()?.cakes ?: emptyList()
                cakeDao.insertAll(cakes)
                emit(Resource.Success(cakes))
            } else {
                // Fallback to local data
                val localCakes = cakeDao.getAllAvailableCakes().first()
                if (localCakes.isNotEmpty()) {
                    emit(Resource.Success(localCakes))
                } else {
                    // Load from JSON as last resort
                    val offlineCakes = loadCakesFromLocalJson()
                    cakeDao.insertAll(offlineCakes)
                    emit(Resource.Success(offlineCakes))
                }
            }
        } catch (e: Exception) {
            // Network error - use cached data
            val localCakes = cakeDao.getAllAvailableCakes().first()
            if (localCakes.isNotEmpty()) {
                emit(Resource.Success(localCakes))
            } else {
                val offlineCakes = loadCakesFromLocalJson()
                if (offlineCakes.isNotEmpty()) {
                    cakeDao.insertAll(offlineCakes)
                    emit(Resource.Success(offlineCakes))
                } else {
                    emit(Resource.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    private fun loadCakesFromLocalJson(): List<Cake> {
        return try {
            val jsonString = context.assets.open("cakes_offline.json")
                .bufferedReader()
                .use { it.readText() }

            val response = Gson().fromJson(jsonString, CakesResponse::class.java)
            response.data ?: response.cakes ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCakeById(cakeId: Int): Flow<Resource<Cake>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getCakeDetail(cakeId)
            if (response.isSuccessful && response.body() != null) {
                val cake = response.body()!!
                cakeDao.insertCake(cake)
                emit(Resource.Success(cake))
            } else {
                val localCake = cakeDao.getCakeById(cakeId)
                if (localCake != null) {
                    emit(Resource.Success(localCake))
                } else {
                    emit(Resource.Error("Cake not found"))
                }
            }
        } catch (e: Exception) {
            val localCake = cakeDao.getCakeById(cakeId)
            if (localCake != null) {
                emit(Resource.Success(localCake))
            } else {
                emit(Resource.Error(e.message ?: "Error loading cake"))
            }
        }
    }
}

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success == true && authResponse.token != null) {
                    tokenManager.saveToken("Bearer ${authResponse.token}")

                    authResponse.user?.let { user ->
                        tokenManager.saveUserInfo(
                            "${user.firstName} ${user.lastName}",
                            user.email
                        )
                    }

                    emit(Resource.Success(authResponse))
                } else {
                    emit(Resource.Error(authResponse.message ?: "Login failed"))
                }
            } else {
                emit(Resource.Error("Invalid credentials"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())

        try {
            val request = RegisterRequest(
                first_name = firstName,
                last_name = lastName,
                username = username,
                email = email,
                password = password,
                password_confirmation = passwordConfirmation
            )

            val response = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!

                if (authResponse.success == true && authResponse.token != null) {
                    tokenManager.saveToken("Bearer ${authResponse.token}")
                    authResponse.user?.let { user ->
                        tokenManager.saveUserInfo(
                            "${user.firstName} ${user.lastName}",
                            user.email
                        )
                    }
                    emit(Resource.Success(authResponse))
                } else {
                    emit(Resource.Error(authResponse.message ?: "Registration failed"))
                }
            } else {
                emit(Resource.Error("Registration failed"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val token = tokenManager.getToken().first()
            if (token != null) {
                apiService.logout(token)
            }
            tokenManager.clearToken()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            tokenManager.clearToken()
            emit(Resource.Success(true))
        }
    }

    fun getToken() = tokenManager.getToken()
}

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {

    fun getLocalCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()

    fun getCartTotal(): Flow<Double> = cartDao.getCartTotal().map { it ?: 0.0 }

    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount().map { it ?: 0 }

    suspend fun addToCart(cake: Cake, quantity: Int, customization: String? = null): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            val cartItem = CartItem(
                cakeId = cake.id,
                cakeName = cake.name,
                price = cake.price,
                image = cake.image,
                quantity = quantity,
                customization = customization
            )

            val existingItem = cartDao.getCartItemByCakeId(cake.id)
            if (existingItem != null) {
                val updated = existingItem.copy(quantity = existingItem.quantity + quantity)
                cartDao.updateCartItem(updated)
            } else {
                cartDao.insertCartItem(cartItem)
            }

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add to cart"))
        }
    }

    suspend fun updateCartItem(cartItem: CartItem): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            cartDao.updateCartItem(cartItem)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update cart"))
        }
    }

    suspend fun removeFromCart(cartItemId: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            cartDao.deleteCartItemById(cartItemId)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to remove item"))
        }
    }

    suspend fun clearCart(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        try {
            cartDao.clearCart()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to clear cart"))
        }
    }
}