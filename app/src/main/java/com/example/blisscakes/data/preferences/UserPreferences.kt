package com.blisscakes.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = intPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    fun getAuthToken(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    suspend fun saveUserData(id: Int, email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = id
            preferences[USER_EMAIL] = email
            preferences[USER_NAME] = name
            preferences[IS_LOGGED_IN] = true
        }
    }

    fun getUserId(): Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    fun getUserEmail(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    fun getUserName(): Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}