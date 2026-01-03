package com.projects.a122mmtv.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.auth.PreLoginViewModel
import com.projects.a122mmtv.screen.PreLoginScreen
import kotlinx.coroutines.delay

@Composable
fun PreLoginWithSplash(
    navController: NavHostController,
    homeSession: HomeSessionViewModel,
    viewModel: PreLoginViewModel
) {
    val context = LocalContext.current

    // UI-only state
    var splashVisible by remember { mutableStateOf(viewModel.showSplash) }
    var allowExit by remember { mutableStateOf(false) }

    // Load data only on cold start
    LaunchedEffect(Unit) {
        if (viewModel.showSplash) {
            viewModel.refresh(context)
        }
    }

    // Minimum logo hold time
    LaunchedEffect(Unit) {
        if (viewModel.showSplash) {
            delay(500)
            allowExit = true
        }
    }

    Box(Modifier.fillMaxSize()) {

        PreLoginScreen(
            navController = navController,
            homeSession = homeSession,
            viewModel = viewModel
        )

        if (splashVisible && viewModel.showSplash) {
            TvSplashOverlay(
                startExit = allowExit && viewModel.isLoaded,
                onExitFinished = {
                    splashVisible = false
                    viewModel.markSplashShown()  // 🔑 THIS is the missing guard
                }
            )
        }
    }
}
