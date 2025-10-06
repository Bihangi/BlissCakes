package com.example.blisscakes.datastore

import androidx.room.*
import com.example.blisscakes.models.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE cakeId = :cakeId")
    suspend fun getCartItemByCakeId(cakeId: Int): CartItem?

    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getCartItemById(id: Int): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT SUM(price * quantity) FROM cart_items")
    fun getCartTotal(): Flow<Double?>

    @Query("SELECT SUM(quantity) FROM cart_items")
    fun getCartItemCount(): Flow<Int?>
}
