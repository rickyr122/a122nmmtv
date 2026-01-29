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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.times
import com.projects.a122mmtv.auth.BannerViewModel
import com.projects.a122mmtv.auth.BannerViewModelFactory
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.components.ViewBanner
import com.projects.a122mmtv.components.ViewContent
import com.projects.a122mmtv.components.ViewContinue
import com.projects.a122mmtv.components.ViewMovieDetail
import com.projects.a122mmtv.components.ViewTopContent
import com.projects.a122mmtv.dataclass.Section
import com.projects.a122mmtv.viewmodels.HomeViewModel

enum class DetailSource {
    BANNER,
    CONTENT
}

enum class InteractionLayer {
    HOME,
    DETAIL
}

@Composable
fun HomePage(
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
    onRequestMenuFocus: () -> Unit,
    isMenuFocused: Boolean,
    onReturnedToMenuFromContent: () -> Unit,
    onDetailVisibilityChanged: (Boolean) -> Unit,
    isDetailOpen: Boolean,
    type: String = "HOM"
) {
    // -1 = banner
    //  0..n = content rows
    var activeRowIndex by rememberSaveable { mutableStateOf(-1) }

    val BANNER_HEIGHT = 420.dp
    val ROW_HEIGHT = 420.dp

    var detailMovieId by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var detailSource by rememberSaveable {
        mutableStateOf<DetailSource?>(null)
    }

    val heroFocusRequester = remember { FocusRequester() }

    LaunchedEffect(detailMovieId) {
        onDetailVisibilityChanged(detailMovieId != null)
    }

//    LaunchedEffect(Unit) {
//        awaitFrame()
//        bannerFocusRequester.requestFocus()
//    }

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
    val categorySections = remember(homeViewModel.allSections) {
        homeViewModel.allSections //.filterIsInstance<Section.Category>()
    }

    val rowFocusRequesters = remember(categorySections.size) {
        List(categorySections.size) { FocusRequester() }
    }

    LaunchedEffect(activeRowIndex) {
        if (activeRowIndex in rowFocusRequesters.indices) {
            rowFocusRequesters[activeRowIndex].requestFocus()
        }
    }

    val shouldBlockSystemBack = remember {
        mutableStateOf(true)
    }

    var restoreBannerInfo by remember { mutableStateOf(false) }

    var interactionLayer by remember { mutableStateOf(InteractionLayer.HOME) }

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
//                if (isDetailOpen) return@onPreviewKeyEvent true
                if (interactionLayer != InteractionLayer.HOME) {
                    return@onPreviewKeyEvent false
                }


                // 🔥 Banner owns ALL DPAD when active
                if (activeRowIndex == -1) {
                    return@onPreviewKeyEvent false
                }

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        val next = activeRowIndex + 1
                        if (activeRowIndex >= categorySections.lastIndex) {
                            true   // consume, but no movement
                        } else {
                            if (next in categorySections.indices) {
                                activeRowIndex = next
                                rowFocusRequesters[next].requestFocus()
                                true
                            } else false
                        }
                    }

                    KeyEvent.KEYCODE_DPAD_UP -> {
                        val prev = activeRowIndex - 1
                        if (prev in categorySections.indices) {
                            activeRowIndex = prev
                            rowFocusRequesters[prev].requestFocus()
                            true
                        } else if (activeRowIndex == 0) {
                            activeRowIndex = -1
                            bannerFocusRequester.requestFocus()
                            true
                        } else false
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
                type = type,
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
                },
                isMenuFocused = isMenuFocused,
                onExitToMenu = onReturnedToMenuFromContent,
                onOpenDetail = { mId ->
                    detailSource = DetailSource.BANNER
                    detailMovieId = mId
                    interactionLayer = InteractionLayer.DETAIL
                },
                restoreInfoFocus = restoreBannerInfo,
                interactionLayer = interactionLayer
            )
        }


        /* =======================
         * CONTENT ROWS
         * ======================= */
        categorySections.forEachIndexed { index, section ->

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
                    .focusRequester(rowFocusRequesters[index])
                    //.focusable()
            ) {
                when (section) {
                    is Section.Continue -> ViewContinue(
                        modifier = Modifier.fillMaxSize(),
                        type = type,
                        horizontalInset = horizontalInset,
                        homeSession = homeSession,
                        isActive = isActive,
                        code = section.code,
                        focusRequester = rowFocusRequesters[index],
                        onMoveDown = {
                            activeRowIndex =
                                (activeRowIndex + 1).coerceAtMost(allSections.lastIndex)
                        },
                        onMoveUp = {
                            activeRowIndex =
                                (activeRowIndex - 1).coerceAtLeast(-1)

                            if (activeRowIndex == -1) {
                                bannerFocusRequester.requestFocus()
                            }
                        },
                        onExitToMenu = onReturnedToMenuFromContent,
                        onOpenDetail = { mId ->
                            detailSource = DetailSource.CONTENT
                            detailMovieId = mId
                            interactionLayer = InteractionLayer.DETAIL
                        },
                        heroFocusRequester = heroFocusRequester,
                        interactionLayer = interactionLayer
                    )

                    is Section.Category ->  ViewContent(
                        modifier = Modifier.fillMaxSize(),
                        horizontalInset = horizontalInset,
                        homeSession = homeSession,
                        isActive = isActive,
                        code = section.code,
                        focusRequester = rowFocusRequesters[index],
                        onMoveDown = {
                            activeRowIndex =
                                (activeRowIndex + 1).coerceAtMost(allSections.lastIndex)
                        },
                        onMoveUp = {
                            activeRowIndex =
                                (activeRowIndex - 1).coerceAtLeast(-1)

                            if (activeRowIndex == -1) {
                                bannerFocusRequester.requestFocus()
                            }
                        },
                        onExitToMenu = onReturnedToMenuFromContent,
                        onOpenDetail = { mId ->
                            detailSource = DetailSource.CONTENT
                            detailMovieId = mId
                            interactionLayer = InteractionLayer.DETAIL
                        },
                        heroFocusRequester = heroFocusRequester,
                        interactionLayer = interactionLayer
                    )

                    is Section.TopContent ->  ViewTopContent(
                        modifier = Modifier.fillMaxSize(),
                        type = type,
                        horizontalInset = horizontalInset,
                        homeSession = homeSession,
                        isActive = isActive,
                        code = section.code,
                        focusRequester = rowFocusRequesters[index],
                        onMoveDown = {
                            activeRowIndex =
                                (activeRowIndex + 1).coerceAtMost(allSections.lastIndex)
                        },
                        onMoveUp = {
                            activeRowIndex =
                                (activeRowIndex - 1).coerceAtLeast(-1)

                            if (activeRowIndex == -1) {
                                bannerFocusRequester.requestFocus()
                            }
                        },
                        onExitToMenu = onReturnedToMenuFromContent,
                        onOpenDetail = { mId ->
                            detailSource = DetailSource.CONTENT
                            detailMovieId = mId
                            interactionLayer = InteractionLayer.DETAIL
                        },
                        heroFocusRequester = heroFocusRequester,
                        interactionLayer = interactionLayer
                    )

//                    else -> Unit
                }
            }
        }

        if (detailMovieId != null) {
            ViewMovieDetail(
                mId = detailMovieId!!,
                isActive = interactionLayer == InteractionLayer.DETAIL,
                horizontalInset = horizontalInset,
                homeSession = homeSession,
                onClose = {
                    detailMovieId = null
                    interactionLayer = InteractionLayer.HOME

                    when (detailSource) {
                        DetailSource.BANNER -> {
                            restoreBannerInfo = true
                            activeRowIndex = -1
                            bannerFocusRequester.requestFocus()
                        }
                        DetailSource.CONTENT -> {
                            restoreBannerInfo = false
                            heroFocusRequester.requestFocus()
                        }
                        else -> {}
                    }

                    detailSource = null
                }
            )
        }


    }
}
