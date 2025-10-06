package com.example.blisscakes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.blisscakes.navigation.NavGraph

@Composable
fun MyApplicationNavigation() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
