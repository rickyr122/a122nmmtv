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
    var splashVisible by remember { mutableStateOf(true) }
    var allowExit by remember { mutableStateOf(false) }


    // Load data once
    LaunchedEffect(Unit) {
        viewModel.refresh(context)
    }

    LaunchedEffect(Unit) {
        delay(500)          // 👈 THIS is the “logo stays still” duration
        allowExit = true
    }


    Box(Modifier.fillMaxSize()) {

        // PreLogin always underneath
        PreLoginScreen(
            navController = navController,
            homeSession = homeSession,
            viewModel = viewModel
        )

        if (splashVisible) {
            TvSplashOverlay(
                startExit = allowExit && viewModel.isLoaded,
                onExitFinished = {
                    splashVisible = false
                }
            )
        }
    }
}




