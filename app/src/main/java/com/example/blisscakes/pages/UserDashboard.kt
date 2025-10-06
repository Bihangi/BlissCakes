package com.example.blisscakes.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.blisscakes.R
import com.example.blisscakes.navigation.NavRoutes

@Composable
fun UserDashboard(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Cart")
    val icons = listOf(R.drawable.ic_home, R.drawable.ic_cart)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                items.forEachIndexed { index, label ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(id = icons[index]), contentDescription = label) },
                        label = { Text(label) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (index == 0) navController.navigate(NavRoutes.Home)
                            else navController.navigate(NavRoutes.Cart)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFE91E63),
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> HomePage(navController)
                1 -> CartPage(navController)
            }
        }
    }

}
