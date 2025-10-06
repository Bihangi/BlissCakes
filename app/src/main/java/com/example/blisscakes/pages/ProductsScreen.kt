package com.example.blisscakes.pages

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.example.blisscakes.models.Product
import com.example.blisscakes.navigation.NavRoutes
import com.example.blisscakes.components.DashboardScaffold
import com.example.blisscakes.network.RetrofitClient
import com.example.blisscakes.datastore.DataStoreManager
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Products(navController: NavHostController) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isTablet = config.screenWidthDp > 600
    val isDarkTheme = isSystemInDarkTheme()

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }

    // Fetch products
    LaunchedEffect(Unit) {
        val dataStore = DataStoreManager(context)
        val token = dataStore.getToken().firstOrNull()

        if (!token.isNullOrEmpty()) {
            RetrofitClient.instance.getProducts("Bearer $token")
                .enqueue(object : Callback<List<Product>> {
                    override fun onResponse(
                        call: Call<List<Product>>,
                        response: Response<List<Product>>
                    ) {
                        if (response.isSuccessful) {
                            products = response.body() ?: emptyList()
                        } else {
                            Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    val filteredProducts = remember(selectedFilters, searchQuery, products) {
        products.filter {
            (selectedFilters.isEmpty() || selectedFilters.contains(it.categoryId.toString())) &&
                    (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
        }
    }

    DashboardScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (isDarkTheme) Color(0xFFB0B0B0) else Color(0xFFFFE4E1),
                            if (isDarkTheme) Color(0xFF121212) else MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                )
        ) {
            HeaderBar(navController, isLandscape)
            SearchBar(searchQuery) { searchQuery = it }
            HeaderSection(isLandscape)
            FilterSection(selectedFilters, { filter ->
                selectedFilters = if (selectedFilters.contains(filter))
                    selectedFilters - filter else selectedFilters + filter
            }, isLandscape)
            ProductGrid(
                products = filteredProducts,
                isTablet = isTablet,
                isLandscape = isLandscape
            ) { product ->
                navController.navigate("detail/${product.id}")
            }
        }
    }
}

@Composable
private fun HeaderBar(navController: NavHostController, isLandscape: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = { navController.navigate(NavRoutes.Login) },
            modifier = Modifier.size(if (isLandscape) 40.dp else 48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(if (isLandscape) 28.dp else 32.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp)
        )
    }
}

@Composable
private fun HeaderSection(isLandscape: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isLandscape) 12.dp else 16.dp)
    ) {
        Text(
            "Cakes",
            fontSize = if (isLandscape) 34.sp else 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun FilterSection(
    selectedFilters: Set<String>,
    onFilterChange: (String) -> Unit,
    isLandscape: Boolean
) {
    val filters = listOf("Theme", "Classic", "Mini", "Desserts")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "FILTER BY",
            fontSize = if (isLandscape) 16.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter) },
                    selected = selectedFilters.contains(filter)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductGrid(
    products: List<Product>,
    isTablet: Boolean,
    isLandscape: Boolean,
    onProductClick: (Product) -> Unit
) {
    val columns = when {
        isTablet && isLandscape -> 4
        isTablet || isLandscape -> 3
        else -> 2
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        products.forEach { product ->
            Box(
                modifier = Modifier
                    .widthIn(min = 0.dp, max = (LocalConfiguration.current.screenWidthDp.dp / columns) - 24.dp)
            ) {
                ProductCard(product, onClick = { onProductClick(product) })
            }
        }
    }
}

@Composable
internal fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "http://10.0.2.2:8000/storage/${product.image}",
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Rs. %.2f".format(product.price), color = MaterialTheme.colorScheme.primary)
        }
    }
}
