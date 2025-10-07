package com.blisscakes.app.ui.browse

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.blisscakes.app.R
import com.blisscakes.app.databinding.ActivityCakeDetailBinding
import com.blisscakes.app.utils.ViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CakeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCakeDetailBinding
    private lateinit var viewModel: CakeDetailViewModel
    private var cakeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCakeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cakeId = intent.getIntExtra("CAKE_ID", 0)
        viewModel = ViewModelProvider(this)[CakeDetailViewModel::class.java]

        setupUI()
        observeViewModel()

        if (cakeId > 0) {
            viewModel.loadCakeDetails(cakeId)
        } else {
            finish()
        }
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnIncrease.setOnClickListener {
            viewModel.incrementQuantity()
        }

        binding.btnDecrease.setOnClickListener {
            viewModel.decrementQuantity()
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart(cakeId)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cake.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE

                        val cake = state.data
                        binding.apply {
                            tvCakeName.text = cake.name
                            tvCakePrice.text = cake.formattedPrice ?: "Rs. ${String.format("%.2f", cake.price)}"
                            tvDescription.text = cake.description
                            tvSize.text = "Size: ${cake.size}"
                            tvFlavor.text = "Flavor: ${cake.flavor}"
                            tvOccasion.text = "Occasion: ${cake.occasion}"
                            tvRating.text = String.format("%.1f ⭐ (%d reviews)",
                                cake.averageRating, cake.totalReviews)

                            if (cake.dietaryOptions.isNotEmpty()) {
                                tvDietaryOptions.text = "Dietary: ${cake.dietaryOptions.joinToString(", ")}"
                                tvDietaryOptions.visibility = View.VISIBLE
                            } else {
                                tvDietaryOptions.visibility = View.GONE
                            }

                            Glide.with(this@CakeDetailActivity)
                                .load(cake.imageUrl ?: cake.image)
                                .placeholder(R.drawable.placeholder_cake)
                                .error(R.drawable.placeholder_cake)
                                .into(ivCakeImage)
                        }
                    }
                    is ViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.quantity.collect { quantity ->
                binding.tvQuantity.text = quantity.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.addToCartState.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.btnAddToCart.isEnabled = false
                    }
                    is ViewState.Success -> {
                        binding.btnAddToCart.isEnabled = true
                        Toast.makeText(this@CakeDetailActivity, "Added to cart!", Toast.LENGTH_SHORT).show()
                        viewModel.resetAddToCartState()
                    }
                    is ViewState.Error -> {
                        binding.btnAddToCart.isEnabled = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        viewModel.resetAddToCartState()
                    }
                    is ViewState.Idle -> {
                        binding.btnAddToCart.isEnabled = true
                    }
                }
            }
        }
    }
}