package com.projects.a122mmtv.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.projects.a122mmtv.auth.BannerViewModel
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.components.ViewBanner

@Composable
fun SeriesPage(
           navController: NavController,
           bannerFocusRequester: FocusRequester,
           upMenuFocusRequester: FocusRequester,
           onBannerFocused: () -> Unit,
           homeSession: HomeSessionViewModel
) {
    val context = LocalContext.current

    val bannerViewModel: BannerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return BannerViewModel(context) as T
            }
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ViewBanner(
            navController = navController,
            type = "TVG",
            currentTabIndex = 0,
            focusRequester = bannerFocusRequester,
            upMenuFocusRequester = upMenuFocusRequester,
            onBannerFocused = onBannerFocused,
            viewModel = bannerViewModel,
            homeSession = homeSession
        )
    }
}