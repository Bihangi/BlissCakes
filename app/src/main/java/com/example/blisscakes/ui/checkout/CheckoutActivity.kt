package com.blisscakes.app.ui.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blisscakes.app.databinding.ActivityCheckoutBinding
import com.blisscakes.app.ui.orders.OrderDetailActivity
import com.blisscakes.app.utils.ViewState
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    private var selectedDeliveryDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Checkout"
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnPlaceOrder.setOnClickListener {
            attemptPlaceOrder()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Delivery Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDeliveryDate = formatter.format(date)

            val displayFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvSelectedDate.text = displayFormatter.format(date)
            binding.tvSelectedDate.visibility = View.VISIBLE
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun attemptPlaceOrder() {
        val address = binding.etAddress.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        // Validate input
        val validationError = viewModel.validateInput(address, phone)
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show()
            return
        }

        // Place order
        viewModel.placeOrder(address, phone, selectedDeliveryDate)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.placeOrderState.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        showLoading(true)
                    }
                    is ViewState.Success -> {
                        showLoading(false)
                        Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                        navigateToOrderDetail(state.data.id)
                    }
                    is ViewState.Error -> {
                        showLoading(false)
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewState.Idle -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnPlaceOrder.isEnabled = !isLoading
        binding.scrollView.isEnabled = !isLoading
    }

    private fun navigateToOrderDetail(orderId: Int) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}