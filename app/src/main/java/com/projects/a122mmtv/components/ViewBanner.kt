package com.projects.a122mmtv.components

import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Bullet
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.BannerUiState
import com.projects.a122mmtv.auth.BannerViewModel
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText
import com.projects.a122mmtv.helper.convertContentRating
import com.projects.a122mmtv.helper.fixEncoding
import com.projects.a122mmtv.utility.BannerStorage
import com.projects.a122mmtv.utility.formatDurationFromMinutes
import org.json.JSONObject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ViewBanner(
    navController: NavController,
    type: String,
    currentTabIndex: Int,
    focusRequester: FocusRequester,
    menuBarFocusRequester: FocusRequester,
    onBannerFocused: () -> Unit,
    viewModel: BannerViewModel,
    homeSession: HomeSessionViewModel,
    //onCollapseRequest: () -> Unit,
    horizontalInset: Dp,
    onEnableMenuFocus: () -> Unit,
    onRequestMenuFocus: () -> Unit,
    onRequestContentFocus: () -> Unit,
    isMenuFocused: Boolean
) {
    var isBannerActive by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(0.dp)

    val uiState = viewModel.bannerState.collectAsState().value
    val userId = homeSession.userId ?: 0

    // 🔥 API call – runs only when `type` changes
    val context = LocalContext.current

    val playFocusRequester = remember { FocusRequester() }
    val infoFocusRequester = remember { FocusRequester() }

    var isOnPlay by remember { mutableStateOf(true) }
    var isOnInfo by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(isBannerActive) {
        if (isBannerActive && !isMenuFocused) {
            playFocusRequester.requestFocus()
        }
    }


    LaunchedEffect(type, userId) {

        // 1️⃣ Try cache FIRST
        val (cachedJson, expired) =
            BannerStorage.loadBanner(context, type, userId)

        if (cachedJson != null && !expired) {
            // 2️⃣ If cache valid → inject into ViewModel
            viewModel.setBannerFromCache(
                AuthApiService.BannerDto(
                    mId = cachedJson.getString("mId"),
                    bdropUrl = cachedJson.getString("bdropUrl"),
                    logoUrl = cachedJson.getString("logoUrl"),
                    mGenre = cachedJson.getString("mGenre"),
                    m_year = cachedJson.getString("m_year"),
                    m_duration = cachedJson.getString("m_duration"),
                    m_content = cachedJson.getString("m_content"),
                    mDescription = cachedJson.getString("mDescription"),
                    playId = cachedJson.getString("playId"),
                    cProgress = cachedJson.getInt("cProgress"),
                    cFlareVid = cachedJson.getString("cFlareVid"),
                    cFlareSrt = cachedJson.getString("cFlareSrt"),
                    gDriveVid = cachedJson.getString("gDriveVid"),
                    gDriveSrt = cachedJson.getString("gDriveSrt")
                )
            )
            return@LaunchedEffect
        }

        // 3️⃣ Cache missing / expired → call API
        viewModel.loadBanner(type)
    }

//    LaunchedEffect(Unit) {
//        // wait for first layout frame
//        kotlinx.coroutines.android.awaitFrame()
//        playFocusRequester.requestFocus()
//    }

//    var hasBannerGivenInitialFocus by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalInset)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isBannerActive = it.hasFocus
                if (it.isFocused) {
                    onBannerFocused()

//                    if (!hasBannerGivenInitialFocus) {
//                        playFocusRequester.requestFocus()
//                        hasBannerGivenInitialFocus = true
//                    }
                }
            }
            .focusable()
            .onPreviewKeyEvent { event ->
                Log.d(
                    "DPAD_FLOW",
                    "Banner preview key=${event.nativeKeyEvent.keyCode} isBannerActive=$isBannerActive"
                )

                if (!isBannerActive) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {

                    KeyEvent.KEYCODE_BACK -> {
                        onEnableMenuFocus()
                        isBannerActive = false
                        menuBarFocusRequester.requestFocus() // ✅ CORRECT TARGET
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        isOnPlay = true
                        isOnInfo = false
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        isOnPlay = false
                        isOnInfo = true
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        if (isOnPlay) {
                            Toast.makeText(context, "Play pressed", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "More Info pressed", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }


                    KeyEvent.KEYCODE_DPAD_UP -> {
                        onRequestMenuFocus()

                        // 🔥 FORCE focus to leave banner
                        focusManager.moveFocus(FocusDirection.Up)

                        true
                    }


//                    KeyEvent.KEYCODE_DPAD_UP -> {
//                        onEnableMenuFocus()
//                        isBannerActive = false
//                        menuBarFocusRequester.requestFocus() // ✅ CORRECT TARGET
//                        true
//                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        onRequestContentFocus()              // 🔥 activate content
                        focusManager.moveFocus(FocusDirection.Down) // 🔥 force focus transfer
                        true
                    }


                    else -> false
                }
            }


            .then(
                if (isBannerActive) {
                    Modifier.border(
                        0.5.dp,
                        Brush.horizontalGradient(
                            listOf(Color.White, Color.LightGray)
                        ),
                        shape
                    )
                } else Modifier
            )
            .clip(shape)
//            .border(2.5.dp, Color.Red)
    ) {

        when (uiState) {

            // ────────────────────
            // Loading
            // ────────────────────
            BannerUiState.Loading,
            BannerUiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(21f / 9f)
                        .background(Color.Black)
                )
            }

            // ────────────────────
            // Error
            // ────────────────────
            is BannerUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(21f / 8f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.msg, color = Color.White)
                }
            }

            // ────────────────────
            // Success
            // ────────────────────
            is BannerUiState.Success -> {
                val banner = uiState.data

                // 🔐 SAVE CACHE (TV only, no color)
                LaunchedEffect(banner.mId, userId) {

                    val json = JSONObject().apply {
                        put("mId", banner.mId)
                        put("bdropUrl", banner.bdropUrl)
                        put("logoUrl", banner.logoUrl)
                        put("mGenre", banner.mGenre)
                        put("m_year", banner.m_year)
                        put("m_duration", banner.m_duration)
                        put("m_content", banner.m_content)
                        put("mDescription", banner.mDescription)
                        put("playId", banner.playId)
                        put("cProgress", banner.cProgress)
                        put("cFlareVid", banner.cFlareVid)
                        put("cFlareSrt", banner.cFlareSrt)
                        put("gDriveVid", banner.gDriveVid)
                        put("gDriveSrt", banner.gDriveSrt)
                    }

                    BannerStorage.saveBanner(
                        context = context,
                        type = type,
                        userId = userId,
                        bannerJson = json.toString()
                    )
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(21f / 9.5f)
                ) {
                    val contentWidth = maxWidth * 0.35f

                    // 🎬 Background
                    AsyncImage(
                        model = banner.bdropUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.65f)
                            .align(Alignment.BottomStart)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // ───────── Content ─────────
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 32.dp, bottom = 28.dp)
                            .width(contentWidth)
                    ) {
                        val logoOffset by animateDpAsState(
                            targetValue = if (isBannerActive) 0.dp else 42.dp,
                            label = "bannerLogoOffset"
                        )

                        // 🖼 Logo
                        AsyncImage(
                            model = banner.logoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(72.dp)
                                .offset(y = logoOffset)
                        )

                        Spacer(Modifier.height(10.dp))

                        // 🧾 Meta row
                        val sType = if (banner.mId.startsWith("MOV")) "Movie" else "Shows"
                        val sDuration = if (banner.mId.startsWith("MOV")) formatDurationFromMinutes(banner.m_duration.toIntOrNull() ?: 0) else banner.m_duration
                        Row(
                            modifier = Modifier
                                .offset(y = logoOffset),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MetaText(sType)
                            Bullets()
                            MetaText(banner.mGenre)
                            Bullets()
                            MetaText(banner.m_year)
                            Bullets()
                            MetaText(sDuration)
                            Bullets()
                            MetaText(banner.m_content.convertContentRating())
                        }

                        Spacer(Modifier.height(8.dp))

                        // 📖 Description
                        AnimatedVisibility(
                            visible = isBannerActive,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = banner.mDescription.fixEncoding(),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        //Spacer(Modifier.height(if (isBannerActive) 12.dp else 0.dp))

                        // ▶ Buttons
//                        AnimatedVisibility(
//                            visible = isBannerActive,
//                            enter = fadeIn(),
//                            exit = fadeOut()
//                        ) {


//                        LaunchedEffect(isBannerActive) {
//                            if (isBannerActive) playFocusRequester.requestFocus()
//                        }
                        val buttonsOffset by animateDpAsState(
                            targetValue = if (isBannerActive) 0.dp else 20.dp,
                            label = "bannerButtonsOffset"
                        )

                        val buttonsAlpha by animateFloatAsState(
                            targetValue = if (isBannerActive) 1f else 0f,
                            label = "bannerButtonsAlpha"
                        )

                        Row(
                            modifier = Modifier
                                .offset(y = buttonsOffset)
                                .alpha(buttonsAlpha)
                                .padding(top = if (isBannerActive) 12.dp else 0.dp)
                        ) {
                            BannerActionButtons(
                                modifier = Modifier
                                    .offset(y = buttonsOffset)
                                    .alpha(buttonsAlpha)
                                    .padding(top = if (isBannerActive) 12.dp else 0.dp),
                                onPlay = {
                                    // TODO: real play navigation
                                },
                                onMoreInfo = {
                                    // TODO: real info navigation
                                },
                                isOnPlay = isOnPlay,
                                isOnInfo = isOnInfo
                            )

                        }

                        //}
                    }
                }
            }
        }
    }
}


//@Composable
//private fun MetaText(text: String) {
//    Text(
//        text = text,
//        color = Color.White,
//        fontSize = 10.sp,
//        fontWeight = FontWeight.Medium,
//        modifier = Modifier.alpha(0.9f)
//    )
//}
//
//@Composable
//private fun Bullet() {
//    Box(
//        modifier = Modifier
//            .padding(horizontal = 4.dp, vertical = 2.dp)
//            .size(4.dp)
//            .clip(CircleShape)
//            .background(Color.Red.copy(alpha = 0.7f))
//    )
//}

@Composable
fun BannerActionButtons(
    modifier: Modifier = Modifier,
    onPlay: () -> Unit,
    onMoreInfo: () -> Unit,
    isOnPlay: Boolean,
    isOnInfo: Boolean
) {
//    var isOnPlay by remember { mutableStateOf(true) }
//    var isOnInfo by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val buttonHeight = 36.dp

    Box(

    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ▶ PLAY
            Row(
                modifier = Modifier
                    .height(buttonHeight)
                    .background(
                        if (isOnPlay) Color.White else Color(0xFF3A3A3A),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = if (isOnPlay) Color.Black else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Play",
                    color = if (isOnPlay) Color.Black else Color.White,
                    fontSize = 14.sp
                )
            }

            // ℹ MORE INFO
            Row(
                modifier = Modifier
                    .height(buttonHeight)
                    .background(
                        if (isOnInfo) Color.White else Color(0xFF3A3A3A),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "More Info",
                    color = if (isOnInfo) Color.Black else Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
private fun BannerButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    disableRight: Boolean = false
) {
    val context = LocalContext.current
    var isFocused by remember { mutableStateOf(false) }

    val bgColor = if (isFocused) Color.White else Color.DarkGray
    val contentColor = if (isFocused) Color.Black else Color.White

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {

//                    KeyEvent.KEYCODE_DPAD_RIGHT ->
//                        if (disableRight) {
//                            true
//                        } else {
//
//                            false
//                        }   // 🔒


                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        Toast.makeText(
                            context,
                            "You click $text button",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }

                    else -> false
                }
            }
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
