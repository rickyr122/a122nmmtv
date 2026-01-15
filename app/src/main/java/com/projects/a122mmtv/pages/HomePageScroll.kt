package com.projects.a122mmtv.pages

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.view.KeyEvent
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.ui.unit.times
import com.projects.a122mmtv.auth.BannerViewModel
import com.projects.a122mmtv.auth.BannerViewModelFactory
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.components.ViewBanner
import com.projects.a122mmtv.components.ViewBanner2
import com.projects.a122mmtv.components.ViewContent
import com.projects.a122mmtv.components.ViewContent2
import com.projects.a122mmtv.dataclass.Section
import com.projects.a122mmtv.viewmodels.HomeViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomePageNoScroll(
    navController: NavController,
    bannerFocusRequester: FocusRequester,
    menuBarFocusRequester: FocusRequester,
    onBannerFocused: () -> Unit,
    homeSession: HomeSessionViewModel,
    horizontalInset: Dp,
    homeViewModel: HomeViewModel = viewModel(),
    scrollState: ScrollState,
    onDisableMenuFocus: () -> Unit,   // 👈 ADD
    onEnableMenuFocus: () -> Unit,     // 👈 ADD
    onRequestMenuFocus: () -> Unit
) {
    // -1 = banner
    //  0..n = content rows
    var activeRowIndex by rememberSaveable { mutableStateOf(-1) }

    val BANNER_HEIGHT = 420.dp
    val ROW_HEIGHT = 420.dp

    LaunchedEffect(Unit) {
        awaitFrame()
        bannerFocusRequester.requestFocus()
    }

    val transition = updateTransition(
        targetState = activeRowIndex,
        label = "homeTransition"
    )

    //var isBannerCollapsed by rememberSaveable { mutableStateOf(false) }
    val bannerHeight by transition.animateDp(
        label = "bannerHeight",
        transitionSpec = { tween(260) }
    ) { state ->
        if (state >= 0) 0.dp else BANNER_HEIGHT
    }


    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val bannerViewModel: BannerViewModel = viewModel(
        factory = BannerViewModelFactory(context)
    )

    val allSections = homeViewModel.allSections

//    val requestMenuFocus: () -> Unit = {
//        menuBarFocusRequester.requestFocus()
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                Log.d(
                    "DPAD_FLOW",
                    "HomePage preview key=${event.nativeKeyEvent.keyCode} activeRowIndex=$activeRowIndex"
                )

                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                // 🔥 Banner owns ALL DPAD when active
                if (activeRowIndex == -1) {
                    return@onPreviewKeyEvent false
                }

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        activeRowIndex =
                            (activeRowIndex + 1)
                                .coerceAtMost(allSections.lastIndex)
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_UP -> {
                        activeRowIndex =
                            (activeRowIndex - 1)
                                .coerceAtLeast(-1)
                        true
                    }

                    else -> false
                }
            }

    ) {

        /* =======================
         * BANNER (row = -1)
         * ======================= */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
                .clipToBounds()
        ) {
            ViewBanner(
                navController = navController,
                type = "HOM",
                currentTabIndex = 0,
                focusRequester = bannerFocusRequester,
                menuBarFocusRequester = menuBarFocusRequester,
                onBannerFocused = {
                    activeRowIndex = -1   // banner = row -1
                    onBannerFocused()
                },
                viewModel = bannerViewModel,
                homeSession = homeSession,
                horizontalInset = horizontalInset,
                onEnableMenuFocus = onEnableMenuFocus,
                onRequestMenuFocus = onRequestMenuFocus,
                onRequestContentFocus = {
                    activeRowIndex = 0    // 🔥 first content row becomes active
                }
            )
        }


        /* =======================
         * CONTENT ROWS
         * ======================= */
        allSections.forEachIndexed { index, section ->

            val isAbove = index < activeRowIndex
            val isActive = index == activeRowIndex

            // collapse rows above
            val rowHeight by animateDpAsState(
                targetValue = if (isAbove) 0.dp else ROW_HEIGHT,
                animationSpec = tween(260),
                label = "rowHeight-$index"
            )


            // vertical positioning
            val rowOffsetY by transition.animateDp(
                label = "rowOffset-$index",
                transitionSpec = { tween(260) }
            ) { state ->
                when {
                    state < 0 ->
                        bannerHeight + (index * ROW_HEIGHT)

                    index < state ->
                        0.dp

                    else ->
                        bannerHeight + ((index - state) * ROW_HEIGHT)
                }
            }

            val rowAlpha by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.45f,
                animationSpec = tween(180),
                label = "rowAlpha-$index"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = rowOffsetY)
                    .height(rowHeight)
                    .alpha(rowAlpha)
                    .clipToBounds()
            ) {
                ViewContent2(
                    modifier = Modifier.fillMaxSize(),
                    horizontalInset = horizontalInset,
                    isActive = isActive
                )
            }
        }

    }
}
