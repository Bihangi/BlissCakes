package com.blisscakes.app.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blisscakes.app.databinding.FragmentCartBinding
import com.blisscakes.app.ui.checkout.CheckoutActivity
import com.blisscakes.app.utils.ViewState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CartViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { itemId, quantity ->
                viewModel.updateQuantity(itemId, quantity)
            },
            onRemoveItem = { itemId ->
                showRemoveDialog(itemId)
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        binding.btnCheckout.setOnClickListener {
            navigateToCheckout()
        }

        binding.btnClearCart.setOnClickListener {
            showClearCartDialog()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCart(forceRefresh = true)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cart.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.swipeRefresh.isRefreshing = true
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false

                        val cartData = state.data
                        if (cartData.items.isEmpty()) {
                            showEmptyCart()
                        } else {
                            showCartWithItems(cartData)
                            cartAdapter.submitList(cartData.items)
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

        lifecycleScope.launch {
            viewModel.cartTotal.collect { total ->
                binding.tvTotalAmount.text = "Rs. ${String.format("%.2f", total)}"
            }
        }
    }

    private fun showEmptyCart() {
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.layoutCart.visibility = View.GONE
    }

    private fun showCartWithItems(cartData: com.blisscakes.app.data.models.CartData) {
        binding.layoutEmpty.visibility = View.GONE
        binding.layoutCart.visibility = View.VISIBLE
        binding.tvItemCount.text = "${cartData.totalItems} items"
    }

    private fun showRemoveDialog(itemId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove this item from cart?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeItem(itemId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearCartDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear Cart")
            .setMessage("Are you sure you want to remove all items from cart?")
            .setPositiveButton("Clear") { _, _ ->
                viewModel.clearCart()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun navigateToCheckout() {
        val intent = Intent(requireContext(), CheckoutActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
