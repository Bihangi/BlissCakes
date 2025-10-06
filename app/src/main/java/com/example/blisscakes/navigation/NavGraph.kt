package com.example.blisscakes.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import com.example.blisscakes.pages.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.Splash) {
        composable(NavRoutes.Splash) { SplashScreen(navController) }
        composable(NavRoutes.Home) { HomeScreen(navController) }
        composable(NavRoutes.Cart) { CartScreen(navController) }
        composable(NavRoutes.Login) { LoginScreen(navController) }
        composable(NavRoutes.Signup) { RegisterScreen(navController) }
        composable(NavRoutes.Products) { ProductsScreen(navController) }
        composable(
            route = "${NavRoutes.Detail}/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(navController, productId)
        }
        composable(NavRoutes.Profile) { ProfileScreen(navController) }
        composable(NavRoutes.Checkout) { CheckoutScreen(navController) }
    }
}