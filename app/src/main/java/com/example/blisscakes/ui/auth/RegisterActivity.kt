package com.blisscakes.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blisscakes.app.databinding.ActivityRegisterBinding
import com.blisscakes.app.ui.main.MainActivity
import com.blisscakes.app.utils.Resource
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            attemptRegister()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun attemptRegister() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        // Validate input
        val validationError = viewModel.validateInput(
            firstName, lastName, username, email, password, confirmPassword
        )
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show()
            return
        }

        // Perform registration
        viewModel.register(
            firstName, lastName, username, email,
            password, confirmPassword,
            phone.ifBlank { null },
            address.ifBlank { null }
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.observe(this@RegisterActivity) { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        Toast.makeText(this@RegisterActivity, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.scrollView.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}