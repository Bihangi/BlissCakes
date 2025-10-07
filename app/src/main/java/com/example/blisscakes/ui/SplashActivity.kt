package com.blisscakes.app.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blisscakes.app.data.preferences.UserPreferences
import com.blisscakes.app.databinding.ActivitySplashBinding
import com.blisscakes.app.ui.auth.LoginActivity
import com.blisscakes.app.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences(this)

        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        lifecycleScope.launch {
            delay(2000) // Show splash for 2 seconds

            val isLoggedIn = userPreferences.isLoggedIn().first()

            val intent = if (isLoggedIn) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }
    }
}