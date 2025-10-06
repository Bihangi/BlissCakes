package com.example.blisscakes.network

import com.example.blisscakes.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Any>>

    @GET("api/user")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<User>

    // Cakes
    @GET("api/cakes")
    suspend fun getCakes(): Response<CakesResponse>

    @GET("api/cakes/{id}")
    suspend fun getCakeDetail(@Path("id") id: Int): Response<Cake>

    @GET("api/cakes/category/{categoryId}")
    suspend fun getCakesByCategory(@Path("categoryId") categoryId: Int): Response<CakesResponse>

    @GET("api/cakes/occasion/{occasion}")
    suspend fun getCakesByOccasion(@Path("occasion") occasion: String): Response<CakesResponse>

    @GET("api/cakes/flavor/{flavor}")
    suspend fun getCakesByFlavor(@Path("flavor") flavor: String): Response<CakesResponse>

    @GET("api/cakes/available")
    suspend fun getAvailableCakes(): Response<CakesResponse>

    // Categories
    @GET("api/categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    @GET("api/categories/{id}")
    suspend fun getCategoryDetail(@Path("id") id: Int): Response<Category>

    // Cart
    @GET("api/cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<Cart>

    @POST("api/cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body request: AddToCartRequest
    ): Response<ApiResponse<CartItemResponse>>

    @PUT("api/cart/{id}")
    suspend fun updateCartItem(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateCartRequest
    ): Response<ApiResponse<CartItemResponse>>

    @DELETE("api/cart/{id}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse<Any>>

    @DELETE("api/cart/clear")
    suspend fun clearCart(@Header("Authorization") token: String): Response<ApiResponse<Any>>

    // Orders
    @GET("api/orders")
    suspend fun getOrders(@Header("Authorization") token: String): Response<OrdersResponse>

    @GET("api/orders/{id}")
    suspend fun getOrderDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<OrderResponse>

    @POST("api/orders")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): Response<OrderResponse>

    @PUT("api/orders/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<OrderResponse>

    // Reviews
    @GET("api/cakes/{cakeId}/reviews")
    suspend fun getReviews(@Path("cakeId") cakeId: Int): Response<ReviewsResponse>

    @POST("api/cakes/{cakeId}/reviews")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Path("cakeId") cakeId: Int,
        @Body review: Map<String, Any>
    ): Response<ApiResponse<Review>>

    // External JSON for master/detail requirement
    @GET
    suspend fun getExternalJson(@Url url: String): Response<CakesResponse>
}