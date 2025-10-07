package com.blisscakes.app.data.remote

import com.blisscakes.app.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(): Response<MessageResponse>

    @GET("user")
    suspend fun getUser(): Response<UserResponse>

    // Cakes
    @GET("cakes/featured")
    suspend fun getFeaturedCakes(): CakesResponse

    @GET("cakes")
    suspend fun getAllCakes(
        @Query("category") category: Int? = null,
        @Query("page") page: Int = 1
    ): CakesResponse

    @GET("cakes/{id}")
    suspend fun getCakeById(@Path("id") id: Int): Response<CakesResponse>

    @GET("cakes/category/{categoryId}")
    suspend fun getCakesByCategory(@Path("categoryId") categoryId: Int): Response<CakesResponse>

    @GET("cakes/search")
    suspend fun searchCakes(@Query("q") query: String): Response<CakesResponse>

    // Categories
    @GET("categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    // Cart
    @GET("cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("cart/add")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<MessageResponse>

    @PUT("cart/update/{id}")
    suspend fun updateCartItem(
        @Path("id") id: Int,
        @Body request: UpdateCartRequest
    ): Response<MessageResponse>

    @DELETE("cart/remove/{id}")
    suspend fun removeFromCart(@Path("id") id: Int): Response<MessageResponse>

    @DELETE("cart/clear")
    suspend fun clearCart(): Response<MessageResponse>

    // Orders
    @GET("orders")
    suspend fun getOrders(): Response<OrdersResponse>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<OrderResponse>

    @POST("orders/place")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<OrderResponse>

    // Reviews
    @GET("cakes/{cakeId}/reviews")
    suspend fun getReviews(@Path("cakeId") cakeId: Int): Response<ReviewsResponse>

    @POST("reviews")
    suspend fun submitReview(@Body request: ReviewRequest): Response<MessageResponse>
}
