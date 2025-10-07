package com.blisscakes.app.ui.browse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blisscakes.app.databinding.FragmentBrowseBinding
import com.blisscakes.app.utils.ViewState
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BrowseFragment : Fragment() {

    private var _binding: FragmentBrowseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BrowseViewModel
    private lateinit var cakeAdapter: CakeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BrowseViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Setup RecyclerView
        cakeAdapter = CakeAdapter { cake ->
            val intent = Intent(requireContext(), CakeDetailActivity::class.java)
            intent.putExtra("CAKE_ID", cake.id)
            startActivity(intent)
        }

        binding.rvCakes.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = cakeAdapter
        }

        // Setup category filter chips
        setupCategoryChips()

        // Setup swipe refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadAllCakes(forceRefresh = true)
        }

        // Setup search
        binding.fabSearch.setOnClickListener {
            // TODO: Implement search functionality
            Snackbar.make(binding.root, "Search coming soon!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupCategoryChips() {
        val categories = listOf(
            "All" to null,
            "Birthday" to 1,
            "Wedding" to 2,
            "Anniversary" to 3,
            "Custom" to 4
        )

        categories.forEach { (name, id) ->
            val chip = Chip(requireContext()).apply {
                text = name
                isCheckable = true
                setOnClickListener {
                    viewModel.filterByCategory(id)
                }
            }
            binding.chipGroupCategories.addView(chip)
        }

        // Select first chip by default
        (binding.chipGroupCategories.getChildAt(0) as? Chip)?.isChecked = true
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cakes.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.swipeRefresh.isRefreshing = true
                        binding.tvEmptyState.visibility = View.GONE
                    }

                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false

                        val cakes = state.data
                        if (cakes.isNullOrEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            cakeAdapter.submitList(emptyList())
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            cakeAdapter.submitList(cakes)
                        }
                    }

                    is ViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        binding.tvEmptyState.visibility = View.VISIBLE
                        Snackbar.make(binding.root, state.message ?: "Something went wrong", Snackbar.LENGTH_LONG).show()
                    }

                    ViewState.Idle -> TODO()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
