package com.projects.a122mmtv.screen

import android.util.Log
import android.view.KeyEvent
import com.projects.a122mmtv.R
import com.projects.a122mmtv.helper.TvScaledBox
import com.projects.a122mmtv.pages.HomePage
import com.projects.a122mmtv.pages.MoviePage
import com.projects.a122mmtv.pages.ProfilePage
import com.projects.a122mmtv.pages.SearchPage
import com.projects.a122mmtv.pages.SeriesPage
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.HomeSessionViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    homeSession: HomeSessionViewModel
) {
    val menuItems = listOf("SEARCH", "Home", "Shows", "Movies", "My Room")

    var selectedIndex by remember { mutableStateOf(1) } // Home default
    var focusedIndex by remember { mutableStateOf(1) }

    val focusRequesters = remember {
        List(menuItems.size) { FocusRequester() }
    }

    var hasHomeAutoFocused by rememberSaveable { mutableStateOf(false) }
    val capsuleShape = RoundedCornerShape(999.dp)
    var isMenuFocused by remember { mutableStateOf(true) }

    var freezeSelection by remember { mutableStateOf(false) }

    TvScaledBox { scale ->

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

        LaunchedEffect(selectedIndex) {
            if (selectedIndex == 1 && !hasHomeAutoFocused) {
                delay(150)
                isMenuFocused = false      // 👈 HERE
                bannerFocusRequester.requestFocus()
                hasHomeAutoFocused = true
            }
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
                                        if (
                                            event.type == KeyEventType.KeyDown &&
                                            event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_DOWN &&
                                            selectedIndex == index
                                        ) {
                                            isMenuFocused = false
                                            bannerFocusRequester.requestFocus()
                                            true
                                        } else false
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

            /** PAGE CONTENT **/
            Box(
                modifier = Modifier
                    .weight(1f)   // 👈 THIS is the key
                    .padding(
                        top = (0 * scale).dp,
                        start = horizontalInset,
                        end = horizontalInset
                    )
            ) {
                when (activePageIndex) {
                    0 -> SearchPage(
                        modifier = modifier,
                        navController = navController
                    )

                    1 -> HomePage(
                        navController = navController,
                        bannerFocusRequester = bannerFocusRequester,
                        upMenuFocusRequester = focusRequesters[selectedIndex],
                        onBannerFocused = {
                            isMenuFocused = false
                        },
                        homeSession = homeSession
                    )


                    2 -> SeriesPage(
                        navController = navController,
                        bannerFocusRequester = bannerFocusRequester,
                        upMenuFocusRequester = focusRequesters[selectedIndex],
                        onBannerFocused = {
                            isMenuFocused = false
                        },
                        homeSession = homeSession
                    )

                    3 -> MoviePage(
                        navController = navController,
                        bannerFocusRequester = bannerFocusRequester,
                        upMenuFocusRequester = focusRequesters[selectedIndex],
                        onBannerFocused = {
                            isMenuFocused = false
                        },
                        homeSession = homeSession
                    )

                    4 -> ProfilePage(
                        modifier,
                        navController
                    )
                }
            }
        }

        /** LEFT PROFILE IMAGE **/
        Box(
            modifier = Modifier
                .width((240 * scale).dp)
                .wrapContentHeight()
                .focusRequester(profileFocusRequester)
                .onFocusChanged {
                    isProfileFocused = it.isFocused
                    freezeSelection = it.isFocused
                }
                .focusable()
                .offset(
                    x = (48 * scale).dp,
                    y = ((topBarHeight - (40 * scale).dp) / 2)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isProfileFocused) {

                // OUTER GREY CONTAINER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.DarkGray, //(0xFF2A2A2A),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(6.dp)
                ) {

                    /** CARD 1 — PROFILE / SWITCH ACCOUNT **/
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(2.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            AsyncImage(
                                model = homeSession.pplink,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size((40 * scale).dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Black, CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = homeSession.userName ?: "",
                                    color = Color.Black,
                                    fontSize = (16 * scale).sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Switch Account",
                                    color = Color.DarkGray,
                                    fontSize = (13 * scale).sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(0.dp))

                    /** CARD 2 — SIGN OUT **/
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray, RoundedCornerShape(2.dp))
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                    ) {
                        ProfileInlineAction(
                            icon = Icons.Filled.Logout,
                            text = "Sign Out",
                            scale = scale,
                            bgColor = Color.DarkGray,
                            textColor = Color.White
                        )
                    }

                    Spacer(Modifier.height(0.dp))

                    /** CARD 3 — EXIT **/
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray, RoundedCornerShape(2.dp))
                            .padding(horizontal = 16.dp, vertical =2.dp)
                    ) {
                        ProfileInlineAction(
                            icon = Icons.Filled.ExitToApp,
                            text = "Exit",
                            scale = scale,
                            bgColor = Color.DarkGray,
                            textColor = Color.White
                        )
                    }
                }

            }  else {

                // 🔹 COMPACT PROFILE (current behavior)
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

                    Spacer(modifier = Modifier.width((6 * scale).dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Profile Menu",
                        tint = Color.LightGray,
                        modifier = Modifier.size((18 * scale).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray.copy(alpha = 0.4f))
            .padding(vertical = 6.dp)
    )
}

@Composable
private fun ProfileInlineAction(
    icon: ImageVector,
    text: String,
    scale: Float,
    bgColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 Avatar spacer to align with profile image above (40dp)
        Box(
            modifier = Modifier
                    .size((40 * scale).dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width((12 * scale).dp)) // ✅ same as header

        Text(
            text = text,
            color = textColor,
            fontSize = (16 * scale).sp,
            fontWeight = FontWeight.Medium
        )
    }
}
