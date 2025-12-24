package com.projects.a122mmtv.screen

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
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
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

    TvScaledBox { scale ->

        LaunchedEffect(Unit) {
            focusRequesters[1].requestFocus()
        }

        var activePageIndex by remember { mutableStateOf(selectedIndex) }

        LaunchedEffect(selectedIndex) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            val iconSz = 40
            /** TOP BAR **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topBarHeight)
                    .padding(horizontal = horizontalInset),
                verticalAlignment = Alignment.CenterVertically
            ) {

                /** LEFT PROFILE IMAGE **/
                val profileFocusRequester = remember { FocusRequester() }
                var isProfileFocused by remember { mutableStateOf(false) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .focusRequester(profileFocusRequester)
                        .onFocusChanged { isProfileFocused = it.isFocused }
                        .focusable()
                        .scale(if (isProfileFocused) 1.1f else 1f)
                ) {
                    AsyncImage(
                        model = "https://res.cloudinary.com/dkfrsrxwp/image/upload/v1761109607/ironman_vp8szl.jpg",
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size((iconSz * scale).dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isProfileFocused) 2.dp else 0.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width((6 * scale).dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Profile Menu",
                        tint = if (isProfileFocused) Color.White else Color.LightGray,
                        modifier = Modifier.size((18 * scale).dp)
                    )
                }

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

                            val backgroundColor = when {
                                isFocused -> Color.White
                                isSelected -> Color.DarkGray
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isFocused -> Color.Black
                                else -> Color.White
                            }

                            val isSearch = index == 0
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
                        }
                    )


                    2 -> SeriesPage(
                        modifier,
                        navController
                    )

                    3 -> MoviePage(
                        modifier,
                        navController
                    )

                    4 -> ProfilePage(
                        modifier,
                        navController
                    )
                }
            }
        }
    }
}

