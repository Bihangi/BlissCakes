package com.blisscakes.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blisscakes.app.data.local.dao.CakeDao
import com.blisscakes.app.data.local.dao.CartDao
import com.blisscakes.app.data.local.dao.OrderDao
import com.blisscakes.app.data.local.entities.CakeEntity
import com.blisscakes.app.data.local.entities.CartEntity
import com.blisscakes.app.data.local.entities.OrderEntity
import com.blisscakes.app.data.local.entities.Converters

@Database(
    entities = [CakeEntity::class, CartEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cakeDao(): CakeDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "blisscakes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}