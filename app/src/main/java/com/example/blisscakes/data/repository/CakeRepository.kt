package com.blisscakes.app.data.repository

import android.content.Context
import com.blisscakes.app.data.models.Cake
import com.blisscakes.app.data.remote.RetrofitClient
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class CakeRepository(private val context: Context) {

    private val apiService = RetrofitClient.apiService

    // In-memory cache
    private var cachedFeaturedCakes: List<Cake>? = null
    private var cachedAllCakes: List<Cake>? = null
    private var lastFetchTime: Long = 0
    private val cacheValidityDuration = 5 * 60 * 1000L // 5 minutes

    suspend fun getFeaturedCakes(forceRefresh: Boolean = false): List<Cake> {
        return withContext(Dispatchers.IO) {
            val currentTime = System.currentTimeMillis()

            if (!forceRefresh && cachedFeaturedCakes != null &&
                (currentTime - lastFetchTime) < cacheValidityDuration) {
                return@withContext cachedFeaturedCakes!!
            }

            try {
                val response = apiService.getFeaturedCakes()
                if (response.success) {
                    cachedFeaturedCakes = response.data
                    lastFetchTime = currentTime
                    response.data
                } else {
                    cachedFeaturedCakes ?: emptyList()
                }
            } catch (e: Exception) {
                cachedFeaturedCakes ?: throw e
            }
        }
    }

    suspend fun getAllCakes(categoryId: Int? = null): List<Cake> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllCakes(category = categoryId)
                if (response.success) {
                    cachedAllCakes = response.data
                    response.data
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                cachedAllCakes ?: emptyList()
            }
        }
    }

    suspend fun getCakeById(cakeId: Int): Cake? {
        return withContext(Dispatchers.IO) {
            try {
                // First check cache
                cachedAllCakes?.find { it.id == cakeId }?.let { return@withContext it }
                cachedFeaturedCakes?.find { it.id == cakeId }?.let { return@withContext it }

                // If not in cache, fetch all cakes and find the one we need
                val allCakes = getAllCakes()
                allCakes.find { it.id == cakeId }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun searchCakes(query: String): Flow<Resource<List<Cake>>> = flow {
        emit(Resource.Loading())

        try {
            // Search in cached cakes first
            val cachedResults = mutableListOf<Cake>()

            cachedAllCakes?.let { cakes ->
                cachedResults.addAll(cakes.filter { cake ->
                    cake.name.contains(query, ignoreCase = true) ||
                            cake.description?.contains(query, ignoreCase = true) == true ||
                            cake.flavor.contains(query, ignoreCase = true)
                })
            }

            if (cachedResults.isNotEmpty()) {
                emit(Resource.Success(cachedResults))
            }

            // Then try to fetch from API if available
            try {
                val response = apiService.searchCakes(query)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        if (cachedResults.isEmpty()) {
                            emit(Resource.Error("No results found"))
                        }
                    }
                } else {
                    if (cachedResults.isEmpty()) {
                        emit(Resource.Error("Failed to search cakes"))
                    }
                }
            } catch (e: Exception) {
                // If API call fails, use cached results
                if (cachedResults.isEmpty()) {
                    // If no cached results, search all cakes locally
                    val allCakes = getAllCakes()
                    val localResults = allCakes.filter { cake ->
                        cake.name.contains(query, ignoreCase = true) ||
                                cake.description?.contains(query, ignoreCase = true) == true ||
                                cake.flavor.contains(query, ignoreCase = true)
                    }

                    if (localResults.isNotEmpty()) {
                        emit(Resource.Success(localResults))
                    } else {
                        emit(Resource.Error(e.message ?: "Search failed"))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}