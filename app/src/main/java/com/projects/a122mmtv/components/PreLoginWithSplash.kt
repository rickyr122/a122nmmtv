package com.projects.a122mmtv.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.auth.PreLoginViewModel
import com.projects.a122mmtv.screen.PreLoginScreen

@Composable
fun PreLoginWithSplash(
    navController: NavHostController,
    homeSession: HomeSessionViewModel,
    viewModel: PreLoginViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.refresh(context)
    }

    Box(Modifier.fillMaxSize()) {

        PreLoginScreen(
            navController = navController,
            homeSession = homeSession,
            viewModel = viewModel
        )

        // ✅ splash ONLY on first app entry
        TvSplashOverlay(
            visible = viewModel.showSplash && !viewModel.isLoaded
        )
    }
}

