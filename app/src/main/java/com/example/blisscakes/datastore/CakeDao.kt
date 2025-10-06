package com.example.blisscakes.datastore

import androidx.room.*
import com.example.blisscakes.models.Cake
import kotlinx.coroutines.flow.Flow

@Dao
interface CakeDao {

    @Query("SELECT * FROM cakes WHERE isAvailable = 1")
    fun getAllAvailableCakes(): Flow<List<Cake>>

    @Query("SELECT * FROM cakes")
    fun getAllCakes(): Flow<List<Cake>>

    @Query("SELECT * FROM cakes WHERE id = :cakeId")
    suspend fun getCakeById(cakeId: Int): Cake?

    @Query("SELECT * FROM cakes WHERE categoryId = :categoryId AND isAvailable = 1")
    fun getCakesByCategory(categoryId: Int): Flow<List<Cake>>

    @Query("SELECT * FROM cakes WHERE occasion = :occasion AND isAvailable = 1")
    fun getCakesByOccasion(occasion: String): Flow<List<Cake>>

    @Query("SELECT * FROM cakes WHERE flavor = :flavor AND isAvailable = 1")
    fun getCakesByFlavor(flavor: String): Flow<List<Cake>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cakes: List<Cake>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCake(cake: Cake)

    @Query("DELETE FROM cakes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM cakes")
    suspend fun getCakeCount(): Int
}