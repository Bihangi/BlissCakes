package com.blisscakes.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blisscakes.app.databinding.ActivityLoginBinding
import com.blisscakes.app.ui.main.MainActivity
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel (you'll need to set up ViewModelFactory)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            // Implement forgot password
            Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Validate input
        val validationError = viewModel.validateInput(email, password)
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show()
            return
        }

        // Perform login
        viewModel.login(email, password)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.observe(this@LoginActivity) { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}