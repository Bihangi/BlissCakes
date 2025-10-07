package com.blisscakes.app.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blisscakes.app.databinding.FragmentHomeBinding
import com.blisscakes.app.ui.browse.CakeAdapter
import com.blisscakes.app.utils.ViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var cakeAdapter: CakeAdapter

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setupUI()
        observeViewModel()
        checkLocationPermission()
    }

    private fun setupUI() {
        // Setup RecyclerView for featured cakes
        cakeAdapter = CakeAdapter { cake ->
            // Navigate to cake detail
            // navController.navigate(HomeFragmentDirections.actionHomeToDetail(cake.id))
        }

        binding.rvFeaturedCakes.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = cakeAdapter
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFeaturedCakes(forceRefresh = true)
        }

        binding.btnRequestLocation.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.featuredCakes.collect { state ->
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
                            binding.rvFeaturedCakes.visibility = View.GONE
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            binding.rvFeaturedCakes.visibility = View.VISIBLE
                            cakeAdapter.submitList(state.data)
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

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.cardLocationRequest.visibility = View.VISIBLE
        } else {
            binding.cardLocationRequest.visibility = View.GONE
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.cardLocationRequest.visibility = View.GONE
                Snackbar.make(binding.root, "Location permission granted", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}