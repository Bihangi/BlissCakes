package com.blisscakes.app

import android.app.Application
import android.content.Context
import com.blisscakes.app.data.local.AppDatabase
import com.blisscakes.app.data.preferences.UserPreferences
import com.blisscakes.app.data.remote.ApiService
import com.blisscakes.app.data.remote.NetworkModule
import com.blisscakes.app.data.repository.*
import com.blisscakes.app.utils.*

class BlissCakesApplication : Application() {

    // Database
    lateinit var database: AppDatabase
        private set

    // Preferences
    lateinit var userPreferences: UserPreferences
        private set

    // API Service
    lateinit var apiService: ApiService
        private set

    // Repositories
    lateinit var authRepository: AuthRepository
        private set
    lateinit var cakeRepository: CakeRepository
        private set
    lateinit var cartRepository: CartRepository
        private set
    lateinit var orderRepository: OrderRepository
        private set

    // Utilities
    lateinit var networkUtils: NetworkUtils
        private set
    lateinit var sensorManager: DeviceSensorManager
        private set
    lateinit var batteryMonitor: BatteryMonitor
        private set
    lateinit var locationHelper: LocationHelper
        private set
    lateinit var cameraHelper: CameraHelper
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        initializeComponents()
    }

    private fun initializeComponents() {
        // Initialize database
        database = AppDatabase.getDatabase(this)

        // Initialize preferences
        userPreferences = UserPreferences(this)

        // Initialize network
        val okHttpClient = NetworkModule.provideOkHttpClient(userPreferences)
        val retrofit = NetworkModule.provideRetrofit(okHttpClient)
        apiService = NetworkModule.provideApiService(retrofit)

        // Initialize repositories
        authRepository = AuthRepository(apiService, userPreferences)
        cakeRepository = CakeRepository(this)
        cartRepository = CartRepository(apiService, database.cartDao())
        orderRepository = OrderRepository(apiService, database.orderDao())

        // Initialize utilities
        networkUtils = NetworkUtils(this)
        sensorManager = DeviceSensorManager(this)
        batteryMonitor = BatteryMonitor(this)
        locationHelper = LocationHelper(this)
        cameraHelper = CameraHelper(this)
    }


    companion object {
        lateinit var instance: BlissCakesApplication
            private set

        fun getAppContext(): Context = instance.applicationContext
    }
}