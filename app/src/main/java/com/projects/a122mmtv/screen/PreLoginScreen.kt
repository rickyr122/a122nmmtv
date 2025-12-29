package com.projects.a122mmtv.screen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.projects.a122mmtv.R
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.AuthRepository
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.auth.TokenStore
import com.projects.a122mmtv.dataclass.AuthNetwork
import com.projects.a122mmtv.getDeviceId
import com.projects.a122mmtv.helper.TvScaledBox

private const val TEMP_PROFILE_URL =
    "https://drive.google.com/uc?export=download&id=1o-g_kS8vTb-gGAAVKBeFkQxBQWoH57ya"

@Composable
fun PreLoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeSession: HomeSessionViewModel
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.pre_login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Optional subtle dark overlay (helps readability on bright backgrounds)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.10f))
        )

        TvScaledBox { s ->
            val logoSize = (96.dp * s).coerceAtLeast(72.dp)

            val circleSize = (160.dp * s).coerceAtLeast(120.dp)
            val circleBorder = (4.dp * s).coerceAtLeast(3.dp)

            val nameTextSize = if ((18.sp * s).value < 16f) 16.sp else 18.sp * s
            val subTextSize = if ((16.sp * s).value < 14f) 14.sp else 16.sp * s

            val gapBetweenTiles = (28.dp * s).coerceAtLeast(18.dp)
            val topPadding = (56.dp * s).coerceAtLeast(28.dp)

            // Focus control
//            val leftFocus = remember { FocusRequester() }
            val rightFocus = remember { FocusRequester() }

            // 0 = profile, 1 = add account
            var lastFocused by rememberSaveable { mutableIntStateOf(0) }

//            // Restore focus when screen becomes visible again
//            LaunchedEffect(lastFocused) {
//                if (lastFocused == 1) {
//                    rightFocus.requestFocus()
//                } else {
//                    leftFocus.requestFocus()
//                }
//            }

            val context = LocalContext.current

            val repo = remember {
                AuthRepository(
                    publicApi = AuthNetwork.publicAuthApi,
                    authedApi = AuthNetwork.authedAuthApi(context),
                    store = TokenStore(context)
                )
            }

            var tvUsers by remember { mutableStateOf<List<AuthApiService.TvUserDto>>(emptyList()) }

            LaunchedEffect(Unit) {
                val deviceId = getDeviceId(context)

                repo.getTvUsersByDeviceId(deviceId)
                    .onSuccess { tvUsers = it }
                    .onFailure { tvUsers = emptyList() }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Logo
                Image(
                    painter = painterResource(id = R.drawable.a122mm_logo),
                    contentDescription = "122MM Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(logoSize)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Center tiles (multiple profiles)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Show every user returned by API
                    tvUsers.forEachIndexed { index, u ->
                        // Create a FocusRequester per profile
                        val fr = remember(u.user_id) { FocusRequester() }

                        // Optional: restore focus to last selected profile index
                        // lastFocused: 0..N-1 for profiles, N for Add Account
                        LaunchedEffect(lastFocused, tvUsers.size) {
                            if (tvUsers.isNotEmpty() && lastFocused == index) {
                                fr.requestFocus()
                            }
                        }

                        ProfileCircleTile(
                            modifier = Modifier.focusRequester(fr),
                            size = circleSize,
                            borderWidth = circleBorder,
                            imageUrl = u.pp_link?.takeIf { it.isNotBlank() } ?: TEMP_PROFILE_URL,
                            title = u.username,
                            email = u.email,
                            titleSize = nameTextSize,
                            onFocused = { lastFocused = index },
                            onClick = {
                                lastFocused = index
                                homeSession.setUser(
                                    id = u.user_id,
                                    name = u.username,
                                    pplink = u.pp_link
                                )
                                navController.navigate("home")
                            }
                        )

                        Spacer(modifier = Modifier.width(gapBetweenTiles))
                    }

                    // Add Account (focus index = tvUsers.size)
                    AddAccountCircleTile(
                        modifier = Modifier.focusRequester(rightFocus),
                        size = circleSize,
                        borderWidth = circleBorder,
                        title = "Add Account",
                        titleSize = subTextSize,
                        onFocused = { lastFocused = tvUsers.size },
                        onClick = {
                            lastFocused = tvUsers.size
                            navController.navigate("login")
                        }
                    )
                }


                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ProfileCircleTile(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp,
    borderWidth: androidx.compose.ui.unit.Dp,
    imageUrl: String,
    title: String,
    email: String,
    titleSize: androidx.compose.ui.unit.TextUnit,
    onFocused: () -> Unit,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val scale by animateFloatAsState(
        targetValue = if (focused) 1.12f else 1.0f,
        label = "focusScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size)
                .scale(scale)
                .clip(CircleShape)
                .border(
                    width = borderWidth,
                    color = if (focused) Color.White else Color.White.copy(alpha = 0.35f),
                    shape = CircleShape
                )
                .onFocusChanged {
                    focused = it.isFocused
                    if (it.isFocused) onFocused()
                }
                .clickable { onClick() }
                .focusable()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Title
        Text(
            text = title,
            color = Color.White,
            fontSize = titleSize,
            fontWeight = FontWeight.Normal
        )

        // Email area (fixed height so title never moves)
        Spacer(modifier = Modifier.height(2.dp))

        Box(
            modifier = Modifier.height(18.dp) // ✅ reserve space always (adjust if you want)
        ) {
            Text(
                text = email,
                color = Color.White.copy(alpha = if (focused && email.isNotBlank()) 0.75f else 0f),
                fontSize = (titleSize.value * 0.75f).sp,
                maxLines = 1
            )
        }

    }
}

@Composable
private fun AddAccountCircleTile(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp,
    borderWidth: androidx.compose.ui.unit.Dp,
    title: String,
    titleSize: androidx.compose.ui.unit.TextUnit,
    onFocused: () -> Unit,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    // Slightly bigger than profile for emphasis
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.12f else 1.0f,
        label = "addAccountFocusScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    if (focused) Color.White else Color.Gray // ✅ WHITE when focused
                )
                .border(
                    width = borderWidth,
                    color = if (focused) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .onFocusChanged {
                    focused = it.isFocused
                    if (it.isFocused) onFocused()
                }
                .clickable { onClick() }
                .focusable()
        ) {
            Text(
                text = "+",
                color = if (focused) Color.Black else Color.White, // ✅ contrast preserved
                fontSize = 56.sp, // slightly larger "+"
                fontWeight = FontWeight.Light
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = titleSize,
            fontWeight = FontWeight.Normal
        )
    }
}


