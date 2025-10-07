package com.blisscakes.app.utils

import android.content.Context
import com.blisscakes.app.data.models.Cake
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object JsonHelper {

    fun loadCakesFromAssets(context: Context, filename: String = "cakes_offline.json"): List<Cake> {
        return try {
            val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Cake>>() {}.type
            Gson().fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun <T> loadJsonFromAssets(context: Context, filename: String, typeToken: TypeToken<T>): T? {
        return try {
            val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, typeToken.type)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}