package com.example.blisscakes.pages

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.blisscakes.DataClasses.CartItems
import com.example.blisscakes.R
import com.example.blisscakes.components.BottomNavigationBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CartPage(navController: NavHostController) {
    val cartItems = remember {
        mutableStateListOf(
            CartItems(1, "Lemon Cake", 1, 1200.0, R.drawable.lemon),
            CartItems(2, "Strawberry Cake", 1, 1300.0, R.drawable.strawberry)
        )
    }

    val deliveryFee = 500.0
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val total = subtotal + deliveryFee
    val orientation = LocalConfiguration.current.orientation
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isDarkTheme = isSystemInDarkTheme()
    val titleColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "My Cart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                cartItems.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E1))
                    ) {
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            CartItemRowPortrait(item, { delta ->
                                item.quantity = (item.quantity + delta).coerceAtLeast(1)
                            }, { cartItems.remove(item) })
                        } else {
                            CartItemRowLandscape(item, { delta ->
                                item.quantity = (item.quantity + delta).coerceAtLeast(1)
                            }, { cartItems.remove(item) })
                        }
                    }
                }

                SummarySection(subtotal, deliveryFee, total)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { cartItems.clear() }
                    ) {
                        Text("Clear Cart")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Order placed successfully!")
                                delay(1500)
                                navController.navigate("products") {
                                    popUpTo("cart") { inclusive = true }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Checkout", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )

        BottomNavigationBar(navController = navController)
    }
}

@Composable
fun CartItemRowPortrait(item: CartItems, onQuantityChange: (Int) -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = item.productName,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, fontWeight = FontWeight.Bold)
            Text("Rs. ${item.price}")
            Spacer(Modifier.height(8.dp))
            QuantityControls(item.quantity, onQuantityChange)
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Item")
        }
    }
}

@Composable
fun CartItemRowLandscape(item: CartItems, onQuantityChange: (Int) -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = item.productName,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, fontWeight = FontWeight.Bold)
            Text("Rs. ${item.price}")
            Spacer(Modifier.height(4.dp))
            QuantityControls(item.quantity, onQuantityChange)
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Item")
        }
    }
}

@Composable
fun QuantityControls(quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedButton(
            onClick = { onQuantityChange(-1) },
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Text("-", fontWeight = FontWeight.Bold)
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        OutlinedButton(
            onClick = { onQuantityChange(1) },
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Text("+", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SummarySection(subtotal: Double, delivery: Double, total: Double) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            "Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(Modifier.height(8.dp))

        SummaryRow("Subtotal", "Rs. %.2f".format(subtotal), textColor)
        SummaryRow("Delivery fee", "Rs. %.2f".format(delivery), textColor)
        Divider(Modifier.padding(vertical = 6.dp))
        SummaryRow("Total", "Rs. %.2f".format(total), textColor, isBold = true)
    }
}

@Composable
fun SummaryRow(label: String, amount: String, color: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
        Text(
            amount,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}
