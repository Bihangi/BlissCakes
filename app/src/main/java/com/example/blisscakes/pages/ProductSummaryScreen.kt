package com.example.blisscakes.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.blisscakes.navigation.NavRoutes

@Composable
fun ProductSummaryScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Order Summary", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFE91E63))
        Spacer(Modifier.height(16.dp))
        Text("Items: Chocolate Cake, Cupcake Box")
        Text("Total: Rs. 1800.00")
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.Home) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue Shopping", color = Color.White)
        }
    }
}
