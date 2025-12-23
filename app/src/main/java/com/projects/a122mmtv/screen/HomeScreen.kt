package com.projects.a122mmtv.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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

    TvScaledBox { scale ->

        LaunchedEffect(Unit) {
            focusRequesters[1].requestFocus()
        }

        LaunchedEffect(focusedIndex) {
            delay(500)
            selectedIndex = focusedIndex
        }

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
                    .height((80 * scale).dp)
                    .padding(horizontal = (24 * scale).dp),
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
                            val isFocused = focusedIndex == index
                            val itemScale by animateFloatAsState(
                                if (isFocused) 1.1f else 1f
                            )

                            Box(
                                modifier = Modifier
                                    .padding(end = (32 * scale).dp)
                                    .focusRequester(focusRequesters[index])
                                    .onFocusChanged {
                                        if (it.isFocused) focusedIndex = index
                                    }
                                    .focusable()
                                    .scale(itemScale)
                                    .padding(vertical = (8 * scale).dp)
                            ) {
                                if (index == 0) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = if (isFocused) Color.White else Color.LightGray,
                                        modifier = Modifier.size((22 * scale).dp)
                                    )
                                } else {
                                    Text(
                                        text = title,
                                        color = if (isFocused) Color.White else Color.LightGray,
                                        fontSize = (18 * scale).sp,
                                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal
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
                    .padding(top = (2 * scale).dp)
            ) {
                when (selectedIndex) {
                    0 -> SearchPage(
                        modifier = modifier,
                        navController = navController
                    )

                    1 -> HomePage(
                        modifier,
                        navController
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

