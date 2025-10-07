package com.blisscakes.app.data.local.dao

import androidx.room.*
import com.blisscakes.app.data.local.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    fun getOrderById(id: Int): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("DELETE FROM orders")
    suspend fun deleteAll()

    @Query("DELETE FROM orders WHERE cachedAt < :timestamp")
    suspend fun deleteOldOrders(timestamp: Long)
}