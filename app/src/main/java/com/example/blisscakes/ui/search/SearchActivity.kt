package com.blisscakes.app.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.blisscakes.app.databinding.ActivitySearchBinding
import com.blisscakes.app.ui.browse.CakeAdapter
import com.blisscakes.app.ui.browse.CakeDetailActivity
import com.blisscakes.app.utils.ViewState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var cakeAdapter: CakeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        cakeAdapter = CakeAdapter { cake ->
            val intent = Intent(this, CakeDetailActivity::class.java)
            intent.putExtra("CAKE_ID", cake.id)
            startActivity(intent)
        }

        binding.rvResults.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = cakeAdapter
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.updateSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.updateSearchQuery(it) }
                return true
            }
        })

        binding.btnClearSearch.setOnClickListener {
            binding.searchView.setQuery("", false)
            viewModel.clearSearch()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.searchResults.collect { state ->
                when (state) {
                    is ViewState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ViewState.Success -> {
                        binding.progressBar.visibility = View.GONE

                        if (state.data.isEmpty()) {
                            binding.tvEmptyState.visibility = View.VISIBLE
                            binding.rvResults.visibility = View.GONE
                        } else {
                            binding.tvEmptyState.visibility = View.GONE
                            binding.rvResults.visibility = View.VISIBLE
                            cakeAdapter.submitList(state.data)
                        }
                    }
                    is ViewState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    is ViewState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvResults.visibility = View.GONE
                    }
                }
            }
        }
    }
}