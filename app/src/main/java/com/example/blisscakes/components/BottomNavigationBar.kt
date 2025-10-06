package com.example.blisscakes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.blisscakes.DataClasses.NavItem
import com.example.blisscakes.R
import com.example.blisscakes.navigation.NavRoutes

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Footer Icons
    val items = listOf(
        NavItem(NavRoutes.Home, R.drawable.ic_home, "Home"),
        NavItem(NavRoutes.Products, R.drawable.ic_cake, "Products"),
        NavItem(NavRoutes.Cart, R.drawable.ic_cart, "Cart"),
        NavItem(NavRoutes.Login, R.drawable.ic_user, "Login")
    )

    val config = LocalConfiguration.current
    val isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val backgroundColor = MaterialTheme.colorScheme.surface
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val indicatorColor = MaterialTheme.colorScheme.secondaryContainer
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(bottom = if (isLandscape) 2.dp else 4.dp)
    ) {
        // Bottom navigation bar
        NavigationBar(
            containerColor = backgroundColor,
            modifier = Modifier.height(if (isLandscape) 70.dp else 80.dp)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(item.iconResId),
                            contentDescription = item.label,
                            tint = if (navController.currentDestination?.route == item.route) selectedColor else unselectedColor,
                            modifier = Modifier.size(if (isLandscape) 20.dp else 24.dp)
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            style = if (isLandscape)
                                MaterialTheme.typography.labelSmall
                            else
                                MaterialTheme.typography.labelMedium,
                            color = if (navController.currentDestination?.route == item.route) selectedColor else unselectedColor
                        )
                    },
                    selected = navController.currentDestination?.route == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(0) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        indicatorColor = indicatorColor,
                        selectedTextColor = selectedColor,
                        unselectedTextColor = unselectedColor
                    )
                )
            }
        }

        // Copyright footer
        if (!isLandscape) {
            Text(
                text = "© 2025 BlissCakes",
                style = MaterialTheme.typography.labelSmall,
                color = unselectedColor,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 2.dp, bottom = 4.dp)
            )
        }
    }
}
