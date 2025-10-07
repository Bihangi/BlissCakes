package com.blisscakes.app.data.local.dao

import androidx.room.*
import com.blisscakes.app.data.local.entities.CakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CakeDao {
    @Query("SELECT * FROM cakes WHERE isAvailable = 1")
    fun getAllCakes(): Flow<List<CakeEntity>>

    @Query("SELECT * FROM cakes WHERE id = :id")
    fun getCakeById(id: Int): Flow<CakeEntity?>

    @Query("SELECT * FROM cakes WHERE categoryId = :categoryId AND isAvailable = 1")
    fun getCakesByCategory(categoryId: Int): Flow<List<CakeEntity>>

    @Query("SELECT * FROM cakes WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchCakes(query: String): Flow<List<CakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCakes(cakes: List<CakeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCake(cake: CakeEntity)

    @Query("DELETE FROM cakes")
    suspend fun deleteAll()

    @Query("DELETE FROM cakes WHERE cachedAt < :timestamp")
    suspend fun deleteOldCakes(timestamp: Long)
}