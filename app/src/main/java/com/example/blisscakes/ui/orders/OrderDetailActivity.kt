package com.blisscakes.app.ui.orders

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blisscakes.app.databinding.ActivityOrderDetailBinding
import com.blisscakes.app.utils.ViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    private var orderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getIntExtra("ORDER_ID", 0)
        viewModel = ViewModelProvider(this)[OrderDetailViewModel::class.java]

        setupUI()
        observeViewModel()

        if (orderId > 0) {
            viewModel.loadOrderDetails(orderId)
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
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.order.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE

                        val order = state.data
                        binding.apply {
                            tvOrderNumber.text = "Order #${order.orderNumber}"
                            tvOrderDate.text = "Placed on: ${order.createdAt}"
                            tvOrderStatus.text = "Status: ${order.status.uppercase()}"
                            tvPaymentStatus.text = "Payment: ${order.paymentStatus.uppercase()}"
                            tvTotalAmount.text = order.formattedTotal ?: "Rs. ${String.format("%.2f", order.totalAmount)}"

                            tvDeliveryAddress.text = order.deliveryAddress
                            tvDeliveryPhone.text = order.deliveryPhone
                            order.deliveryDate?.let {
                                tvDeliveryDate.text = "Delivery Date: $it"
                                tvDeliveryDate.visibility = View.VISIBLE
                            } ?: run {
                                tvDeliveryDate.visibility = View.GONE
                            }

                            // Setup order items recycler view
                            rvOrderItems.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
                            rvOrderItems.adapter = OrderItemAdapter(order.items)
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
    }
}