package com.projects.a122mmtv.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.projects.a122mmtv.auth.BannerViewModel
import com.projects.a122mmtv.auth.BannerViewModelFactory
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.components.ViewBanner
import com.projects.a122mmtv.components.ViewContent
import kotlinx.coroutines.delay

@Composable
fun HomePage(
    navController: NavController,
    bannerFocusRequester: FocusRequester,
    upMenuFocusRequester: FocusRequester,
    onBannerFocused: () -> Unit,
    homeSession: HomeSessionViewModel,
    horizontalInset: Dp
) {
    val context = LocalContext.current

    // ✅ CORRECT ViewModel creation
    val bannerViewModel: BannerViewModel = viewModel(
        factory = BannerViewModelFactory(context)
    )

    val contentFirstItemFR = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // ========== BANNER ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalInset)
        ) {
            ViewBanner(
                navController = navController,
                type = "HOM",
                currentTabIndex = 0,
                focusRequester = bannerFocusRequester,
                upMenuFocusRequester = upMenuFocusRequester,
                onBannerFocused = onBannerFocused,
                viewModel = bannerViewModel,   // ✅ pass explicitly
                homeSession = homeSession,
                onCollapseRequest = {
                    // NO-OP
                }
            )
        }



        //Spacer(modifier = Modifier.height(16.dp))

        // ========== CONTENT ==========
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = horizontalInset)
//        ) {
//            ViewContent(
//                firstItemFocusRequester = contentFirstItemFR,
//                onRequestShowBanner = {
//                    // NO-OP
//                }
//            )
//        }
    }
}




