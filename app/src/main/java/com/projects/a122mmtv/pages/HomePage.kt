package com.projects.a122mmtv.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.projects.a122mmtv.components.ViewBanner2
import com.projects.a122mmtv.components.ViewContent
import com.projects.a122mmtv.dataclass.Section
import com.projects.a122mmtv.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    navController: NavController,
    bannerFocusRequester: FocusRequester,
    upMenuFocusRequester: FocusRequester,
    onBannerFocused: () -> Unit,
    homeSession: HomeSessionViewModel,
    horizontalInset: Dp,
    homeViewModel: HomeViewModel = viewModel(),
    scrollState: ScrollState
) {
    val context = LocalContext.current
    val isLoading = homeViewModel.isLoading
    val allSections = homeViewModel.allSections
    val coroutineScope = rememberCoroutineScope()


    // ✅ CORRECT ViewModel creation
    val bannerViewModel: BannerViewModel = viewModel(
        factory = BannerViewModelFactory(context)
    )

    val contentFirstItemFR = remember { FocusRequester() }
    var isBannerCollapsed by rememberSaveable { mutableStateOf(false) }

    val bannerOffsetY by animateDpAsState(
        targetValue = if (isBannerCollapsed) (-410).dp else 0.dp,
        animationSpec = tween(durationMillis = 420),
        label = "bannerCollapse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = bannerOffsetY)
    ) {

        // ========== BANNER ==========
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
                isBannerCollapsed = true
                coroutineScope.launch {
                    delay(420) // wait animation
                    contentFirstItemFR.requestFocus()
                }
            },
            horizontalInset = horizontalInset
        )

//        ViewBanner2(
//            focusRequester = bannerFocusRequester,
//            horizontalInset = horizontalInset
//        )

        ViewContent(
            firstItemFocusRequester = contentFirstItemFR,
            onRequestShowBanner = {
                isBannerCollapsed = false
                coroutineScope.launch {
                    delay(420)
                    bannerFocusRequester.requestFocus()
                }
            },
            onRequestFocusSelf = {
                contentFirstItemFR.requestFocus()
            },
            horizontalInset = horizontalInset
        )

//        allSections.forEach { section ->
//            when (section) {
//                is Section.Category -> ViewContent(
//                    firstItemFocusRequester = contentFirstItemFR,
//                    onRequestShowBanner = {
//                        coroutineScope.launch {
//                            scrollState.animateScrollTo(0)
//                        }
//                        bannerFocusRequester.requestFocus()
//                    },
//                    onRequestFocusSelf = {
//                        contentFirstItemFR.requestFocus()
//                    },
//                    horizontalInset = horizontalInset
//                )
//                else -> Unit
//            }
//        }


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




