package com.example.blisscakes.datastore

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.blisscakes.models.CartItem
import com.example.blisscakes.models.Cake
import com.example.blisscakes.datastore.CakeDao
import com.example.blisscakes.datastore.CartDao


@Database(
    entities = [Cake::class, CartItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cakeDao(): CakeDao
    abstract fun cartDao(): CartDao
}