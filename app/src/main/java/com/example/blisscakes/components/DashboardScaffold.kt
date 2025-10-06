package com.example.blisscakes.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun DashboardScaffold(
    navController: NavHostController,
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        content(innerPadding)
    }
}