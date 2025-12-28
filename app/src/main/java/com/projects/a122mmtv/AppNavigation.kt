package com.projects.a122mmtv

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.screen.HomeScreen
import com.projects.a122mmtv.screen.LoginScreen
import com.projects.a122mmtv.screen.PreLoginScreen

@Composable
fun AppNavigation (
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val homeSession: HomeSessionViewModel = viewModel()
    NavHost(navController = navController  , startDestination = "prelogin") {


        composable("prelogin") {
            PreLoginScreen(modifier, navController, homeSession = homeSession)
        }

        composable("login") {
           LoginScreen(modifier, navController)
        }

        composable("home") {
            HomeScreen(modifier, navController, homeSession = homeSession)
        }
    }
}