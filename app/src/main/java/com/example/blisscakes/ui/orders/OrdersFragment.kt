package com.blisscakes.app.ui.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blisscakes.app.databinding.FragmentOrdersBinding
import com.blisscakes.app.utils.ViewState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OrdersViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[OrdersViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        orderAdapter = OrderAdapter { order ->
            val intent = Intent(requireContext(), OrderDetailActivity::class.java)
            intent.putExtra("ORDER_ID", order.id)
            startActivity(intent)
        }

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderAdapter
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadOrders(forceRefresh = true)
        }

        // Setup tabs for order status filter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> filterOrders("all")
                    1 -> filterOrders("pending")
                    2 -> filterOrders("confirmed")
                    3 -> filterOrders("delivered")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.orders.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.swipeRefresh.isRefreshing = true
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false

                        if (state.data.isEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.rvOrders.visibility = View.GONE
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            binding.rvOrders.visibility = View.VISIBLE
                            orderAdapter.submitList(state.data)
                        }
                    }
                    is ViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun filterOrders(status: String) {
        // Implement filtering logic based on status
        if (status == "all") {
            viewModel.loadOrders()
        } else {
            val filteredOrders = viewModel.getOrdersByStatus(status)
            orderAdapter.submitList(filteredOrders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}