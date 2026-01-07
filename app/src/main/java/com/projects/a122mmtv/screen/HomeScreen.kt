package com.projects.a122mmtv.screen

import android.app.Activity
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import com.projects.a122mmtv.R
import com.projects.a122mmtv.helper.TvScaledBox
import com.projects.a122mmtv.pages.HomePage
import com.projects.a122mmtv.pages.MoviePage
import com.projects.a122mmtv.pages.ProfilePage
import com.projects.a122mmtv.pages.SearchPage
import com.projects.a122mmtv.pages.SeriesPage
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.HomeSessionViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    homeSession: HomeSessionViewModel
) {
    var showBackMenu by remember { mutableStateOf(false) }
    var backJustClosedMenu by remember { mutableStateOf(false) }

    val backMenuFocusRequester = remember { FocusRequester() }

    val menuItems = listOf("SEARCH", "Home", "Shows", "Movies", "My Room")

    var selectedIndex by remember { mutableStateOf(1) } // Home default
    var focusedIndex by remember { mutableStateOf(1) }

    val focusRequesters = remember {
        List(menuItems.size) { FocusRequester() }
    }

    var hasHomeAutoFocused by remember { mutableStateOf(false) }
    val capsuleShape = RoundedCornerShape(999.dp)
    var isMenuFocused by remember { mutableStateOf(true) }

    var freezeSelection by remember { mutableStateOf(false) }

    TvScaledBox { scale ->

        // 1️⃣ BACK closes popup (highest priority)
        BackHandler(enabled = showBackMenu) {
            showBackMenu = false
            focusRequesters[selectedIndex].requestFocus()
        }

        // 2️⃣ BACK opens popup (only when popup is NOT visible)
        BackHandler(enabled = !showBackMenu && isMenuFocused) {
            showBackMenu = true
        }

        // 3️⃣ Optional: consume BACK everywhere else (prevents nav pop)
        BackHandler(enabled = !showBackMenu && !isMenuFocused) {
            // do nothing
        }


        LaunchedEffect(showBackMenu) {
            if (!showBackMenu) {
                // wait one frame so focus events finish
                backJustClosedMenu = false
            }
        }

        LaunchedEffect(Unit) {
            focusRequesters[1].requestFocus()
        }

        var activePageIndex by remember { mutableStateOf(selectedIndex) }

//        LaunchedEffect(selectedIndex) {
//            delay(500) // debounce duration
//            activePageIndex = selectedIndex
//        }
        LaunchedEffect(selectedIndex, freezeSelection) {
            if (freezeSelection) return@LaunchedEffect

            delay(500) // debounce duration
            activePageIndex = selectedIndex
        }

        val bannerFocusRequester = remember { FocusRequester() }

//        LaunchedEffect(selectedIndex) {
//            if (selectedIndex == 1 && !hasHomeAutoFocused) {
//                delay(150)
//                isMenuFocused = false      // 👈 HERE
//                bannerFocusRequester.requestFocus()
//                hasHomeAutoFocused = true
//            }
//        }

        LaunchedEffect(Unit) {
            // Cold app start only
            delay(200)
            isMenuFocused = false
            bannerFocusRequester.requestFocus()
            //hasHomeAutoFocused = true
        }

        val topBarHeight = (80 * scale).dp
        val horizontalInset = (48 * scale).dp
        val profileFocusRequester = remember { FocusRequester() }
        var isProfileFocused by remember { mutableStateOf(false) }
        val iconSz = 40

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
//            Log.d("User_Id::check", "User_id -> ${homeSession.userId}")
//            Log.d("User_name::check", "user_name -> ${homeSession.userName}")
//            Log.d("pp_link::check", "ppLink -> ${homeSession.pplink}")


            /** TOP BAR **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .padding(horizontal = horizontalInset),
//                    .onPreviewKeyEvent { event ->
//                        if (
//                            event.type == KeyEventType.KeyDown &&
//                            event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK &&
//                            isMenuFocused &&
//                            !showBackMenu
//                        ) {
//                            showBackMenu = true
//                            true // consume BACK
//                        } else {
//                            false
//                        }
//                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                /** CENTER MENU COLUMN **/
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        menuItems.forEachIndexed { index, title ->
                            val isFocused = isMenuFocused && focusedIndex == index
                            val isSelected = !isFocused && selectedIndex == index

                            val isSearch = index == 0

                            val backgroundColor = when {
                                isProfileFocused -> Color.Black
                                isSearch && isProfileFocused -> Color.DarkGray   // 👈 force gray
                                isFocused -> Color.White
                                isSelected -> Color.DarkGray
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isSearch && isProfileFocused -> Color.White      // 👈 force white
                                isFocused -> Color.Black
                                else -> Color.White
                            }


                            //val isSearch = index == 0
                            val shape = if (index == 0) CircleShape else capsuleShape

                            Box(
                                contentAlignment = Alignment.Center, // 👈 THIS is the fix
                                modifier = Modifier
                                    .padding(end = (20 * scale).dp)
                                    .focusRequester(focusRequesters[index])
                                    .onFocusChanged {
                                        if (it.isFocused) {
                                            focusedIndex = index
                                            selectedIndex = index
                                            isMenuFocused = true
                                        }
                                    }
                                    .onPreviewKeyEvent { event ->
                                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                                        when (event.nativeKeyEvent.keyCode) {

                                            // 🔒 DISABLE UP when menu is focused
                                            KeyEvent.KEYCODE_DPAD_UP ->
                                                isMenuFocused

                                            // ✅ DOWN goes to banner
                                            KeyEvent.KEYCODE_DPAD_DOWN ->
                                                if (isMenuFocused && selectedIndex == index) {
                                                    isMenuFocused = false
                                                    bannerFocusRequester.requestFocus()
                                                    true
                                                } else false

                                            else -> false
                                        }
                                    }

                                    .focusable()
                                    .clip(shape)
                                    .background(backgroundColor)
                                    .then(
                                        if (isSearch) {
                                            Modifier.size((44 * scale).dp)
                                        } else {
                                            Modifier.padding(
                                                horizontal = (24 * scale).dp,
                                                vertical = (6 * scale).dp
                                            )
                                        }
                                    )
                            )
                            {
                                if (isSearch) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = textColor,
                                        modifier = Modifier.size((20 * scale).dp)
                                    )
                                } else {
                                    Text(
                                        text = title,
                                        color = textColor,
                                        fontSize = (18 * scale).sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }


                        }
                    }
                }

                /** RIGHT LOGO COLUMN **/
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.a122mm_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.height((iconSz * scale).dp)
                    )
                }
            }

            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier.weight(1f)
            ) {
                ContentScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    selectedIndex = activePageIndex,
                    bannerFocusRequester = bannerFocusRequester,
                    upMenuFocusRequester = focusRequesters[selectedIndex],
                    homeSession = homeSession,
                    horizontalInset = horizontalInset,
                    scrollState = scrollState,
                    onBannerFocused = {
                        isMenuFocused = false
                    }
                )
            }

        }

        /** LEFT PROFILE IMAGE **/
        Box(
            modifier = Modifier
                .width((240 * scale).dp)
                .wrapContentHeight()
                .focusRequester(profileFocusRequester)
//                .onFocusChanged {
//                    isProfileFocused = it.isFocused
//                    freezeSelection = it.isFocused
//                }
//                .focusable()
                .offset(
                    x = (48 * scale).dp,
                    y = ((topBarHeight - (40 * scale).dp) / 2)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
                //  COMPACT PROFILE (current behavior)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    AsyncImage(
                        model = homeSession.pplink,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size((iconSz * scale).dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

//                    Spacer(modifier = Modifier.width((6 * scale).dp))
//
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowDown,
//                        contentDescription = "Profile Menu",
//                        tint = Color.LightGray,
//                        modifier = Modifier.size((18 * scale).dp)
//                    )
                }
        }
        val context = LocalContext.current

        if (showBackMenu) {
            BackActionPopup(
                scale = scale,
                topBarHeight = topBarHeight,
                homeSession = homeSession,   // ✅ REQUIRED
                onDismiss = {
                    showBackMenu = false
                    focusRequesters[selectedIndex].requestFocus()
                },
                onSwitchAccount = {
                    showBackMenu = false
                    navController.navigate("prelogin") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onSignOut = {
                    // TODO sign out
                },
                onExit = {
                    (context as? Activity)?.finishAffinity()
                },
                focusRequester = backMenuFocusRequester
            )

        }

    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    selectedIndex: Int,
    bannerFocusRequester: FocusRequester,
    upMenuFocusRequester: FocusRequester,
    homeSession: HomeSessionViewModel,
    horizontalInset: Dp,
    scrollState: ScrollState,
    onBannerFocused: () -> Unit
) {
    Column(
        modifier = modifier
            //.verticalScroll(scrollState)
            .fillMaxSize()
    ) {
        when (selectedIndex) {

            // HOME
            1 -> HomePage(
                navController = navController,
                bannerFocusRequester = bannerFocusRequester,
                upMenuFocusRequester = upMenuFocusRequester,
                onBannerFocused = onBannerFocused,
                homeSession = homeSession,
                horizontalInset = horizontalInset,
                scrollState = scrollState
            )

            // SEARCH
            0 -> SearchPage(
                modifier = Modifier.fillMaxSize(),
                navController = navController
            )

            // SHOWS
            2 -> SeriesPage(
                navController = navController,
                bannerFocusRequester = bannerFocusRequester,
                upMenuFocusRequester = upMenuFocusRequester,
                onBannerFocused = onBannerFocused,
                homeSession = homeSession
            )

            // MOVIES
            3 -> MoviePage(
                navController = navController,
                bannerFocusRequester = bannerFocusRequester,
                upMenuFocusRequester = upMenuFocusRequester,
                onBannerFocused = onBannerFocused,
                homeSession = homeSession
            )

            // PROFILE
            4 -> ProfilePage(
                modifier = Modifier.fillMaxSize(),
                navController = navController
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}


@Composable
private fun BackActionPopup(
    scale: Float,
    topBarHeight: Dp,
    homeSession: HomeSessionViewModel,
    onDismiss: () -> Unit,
    onSwitchAccount: () -> Unit,
    onSignOut: () -> Unit,
    onExit: () -> Unit,
    focusRequester: FocusRequester
) {
    val headerFocus = remember { FocusRequester() }
    val total = 3 // header + 2 items

    // ✅ THIS is where it goes
    LaunchedEffect(Unit) {
        headerFocus.requestFocus()
    }

    val profileIconSize = (40 * scale).dp
    val profileTopY = (topBarHeight - profileIconSize) / 2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(
                x = (26 * scale).dp,
                y = (-72).dp   // ⬆ popup ABOVE icon
        ),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .padding(top = (100 * scale).dp)
                .width((256 * scale).dp)
                .background(Color(0xFF3A3A3A), RoundedCornerShape(0.dp))
                .padding(4.dp)
        ) {

            BackMenuHeaderItem(
                scale = scale,
                name = homeSession.userName ?: "",
                avatarUrl = homeSession.pplink,
                index = 0,
                totalCount = total,
                focusRequester = headerFocus,
                onClick = onSwitchAccount
            )

            TvPopupIconItem(
                text = "Sign Out",
                icon = Icons.Filled.Logout,
                scale = scale,
                index = 1,
                totalCount = total,
                onClick = onSignOut
            )

            TvPopupIconItem(
                text = "Exit RR Movies",
                icon = Icons.Filled.ExitToApp,
                scale = scale,
                index = 2,
                totalCount = total,
                onClick = onExit
            )
        }
    }
}


@Composable
private fun BackMenuHeaderItem(
    scale: Float,
    name: String,
    avatarUrl: String?,
    index: Int,
    totalCount: Int,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    val bgColor = if (focused) Color.White else Color(0xFF3A3A3A)
    val primaryTextColor = if (focused) Color.Black else Color.White
    val secondaryTextColor = if (focused) Color.DarkGray else Color.LightGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(
                if (focusRequester != null)
                    Modifier.focusRequester(focusRequester)
                else Modifier
            )
            .background(bgColor, RoundedCornerShape(0.dp))
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT,
                    KeyEvent.KEYCODE_DPAD_RIGHT -> true
                    KeyEvent.KEYCODE_DPAD_UP -> index == 0
                    KeyEvent.KEYCODE_DPAD_DOWN -> index == totalCount - 1
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        onClick()
                        true
                    }
                    else -> false
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile",
            modifier = Modifier
                .size((40 * scale).dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = name,
                color = primaryTextColor,
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Switch Account",
                color = secondaryTextColor,
                fontSize = (13 * scale).sp
            )
        }
    }
}

@Composable
private fun TvPopupIconItem(
    text: String,
    icon: ImageVector,
    scale: Float,
    index: Int,
    totalCount: Int,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    val bgColor = if (focused) Color.White else Color(0xFF3A3A3A)
    val textColor = if (focused) Color.Black else Color.White
    val iconColor = textColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .then(
                if (focusRequester != null)
                    Modifier.focusRequester(focusRequester)
                else Modifier
            )
            .background(bgColor, RoundedCornerShape(0.dp))
            .onFocusChanged { focused = it.isFocused }
            .focusable()
            .padding(horizontal = 12.dp)  // ⬅ outer padding only
            .onPreviewKeyEvent {
            if (it.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
            when (it.nativeKeyEvent.keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,
                KeyEvent.KEYCODE_ENTER -> {
                    onClick()
                    true
                }
                KeyEvent.KEYCODE_DPAD_UP -> index == 0
                KeyEvent.KEYCODE_DPAD_DOWN -> index == totalCount - 1
                KeyEvent.KEYCODE_DPAD_RIGHT -> true
                else -> false
            }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 ICON SLOT (fixed + centered)
        Box(
            modifier = Modifier
                .size(40.dp),               // ⬅ fixed icon column
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        // 🔹 TEXT SLOT (left aligned)
        Text(
            text = text,
            color = textColor,
            fontSize = (16 * scale).sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

}