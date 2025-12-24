package com.projects.a122mmtv

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.projects.a122mmtv.screen.HomeScreen
import com.projects.a122mmtv.screen.LoginScreen
import com.projects.a122mmtv.screen.PreLoginScreen

@Composable
fun AppNavigation (
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController  , startDestination = "home") {
        composable("prelogin") {
            PreLoginScreen(modifier, navController)
        }

        composable("login") {
           LoginScreen(modifier, navController)
        }

        composable("home") {
            HomeScreen(modifier, navController)
        }
    }
}