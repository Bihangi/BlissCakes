package com.blisscakes.app.data.remote

import com.blisscakes.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            userPreferences.getAuthToken().first()
        }

        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader("Authorization", "Bearer $it")
        }
        request.addHeader("Accept", "application/json")

        return chain.proceed(request.build())
    }
}
