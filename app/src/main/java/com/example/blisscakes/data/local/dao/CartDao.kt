package com.blisscakes.app.data.local.dao

import androidx.room.*
import com.blisscakes.app.data.local.entities.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE cakeId = :cakeId")
    suspend fun getCartItem(cakeId: Int): CartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    @Update
    suspend fun updateCartItem(item: CartEntity)

    @Delete
    suspend fun deleteCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT SUM(quantity * price) FROM cart_items")
    fun getCartTotal(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM cart_items")
    fun getCartItemCount(): Flow<Int>
}
