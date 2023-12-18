package com.example.myapplication

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.edit.EditScreen
import com.example.myapplication.ui.screens.image.ImageScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.ImageScreen.name
    ) {
        composable(AppRoutes.ImageScreen.name) {
            ImageScreen(navController = navController)
        }
        composable("${AppRoutes.EditScreen.name}/{uriPath}") {  backStackEntry ->
            val uriPath = backStackEntry.arguments?.getString("uriPath").toString()
            val uri = Uri.parse(uriPath)
            EditScreen(uri = uri, navController = navController)
        }
    }
}

enum class AppRoutes {
    ImageScreen, EditScreen
}