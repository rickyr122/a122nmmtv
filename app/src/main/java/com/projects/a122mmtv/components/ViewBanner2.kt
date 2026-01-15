package com.projects.a122mmtv.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.KeyEvent
import androidx.compose.material3.Text
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.BannerViewModel
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText
import kotlinx.coroutines.android.awaitFrame

@Composable
fun ViewBanner2(
    navController: NavController,
    type: String,
    currentTabIndex: Int,
    focusRequester: FocusRequester,
    menuBarFocusRequester: FocusRequester,
    onBannerFocused: () -> Unit,
    viewModel: BannerViewModel,
    homeSession: HomeSessionViewModel,
    onCollapseRequest: () -> Unit,
    horizontalInset: Dp,
    onEnableMenuFocus: () -> Unit,
    onRequestMenuFocus: () -> Unit
) {
    val context = LocalContext.current
    var isFocused by remember { mutableStateOf(false) }
    var selectedButton by rememberSaveable { mutableStateOf(0) }

    var isBannerActive by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(0.dp)

    var isOnPlay by remember { mutableStateOf(true) }
    var isOnInfo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .focusRequester(focusRequester)   // 🔥 external owner
            .onFocusChanged {
                Log.d("BANNER2", "focused=${it.isFocused}")
                isFocused = it.isFocused
                if (it.isFocused) {
                    selectedButton = 0
                    onBannerFocused()
                }
            }
            .focusable()
            .onPreviewKeyEvent { event ->
                if (!isFocused) return@onPreviewKeyEvent false
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        selectedButton = 0; true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        selectedButton = 1; true
                    }
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_ENTER -> {
                        val label =
                            if (selectedButton == 0) "Play" else "More Info"

                        Toast.makeText(
                            context,
                            "$label is pressed",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    else -> false
                }
            }
    ){

        /* =======================
         * BACKGROUND IMAGE
         * ======================= */
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w1280/34jW8LvjRplM8Pv06cBFDpLlenR.jpg",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isFocused)
                        Modifier.border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    else Modifier
                )
        )

        /* =======================
         * GRADIENT OVERLAY
         * ======================= */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        /* =======================
         * CONTENT
         * ======================= */
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
                .fillMaxWidth(0.7f)
        ) {

            /* LOGO */
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500/hvntb28ngQ2uwLQRCy12htg1Hn9.png",
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(64.dp)
            )

            Spacer(Modifier.height(12.dp))

            /* META DATA */
            Row(verticalAlignment = Alignment.CenterVertically) {
                MetaText("Movie")
                Bullets()
                MetaText("Action")
                Bullets()
                MetaText("2019")
                Bullets()
                MetaText("2h 9m")
                Bullets()
                MetaText("13+")
            }

            Spacer(Modifier.height(12.dp))

            /* DESCRIPTION (only when focused) */
            if (isFocused) {
                Text(
                    text =
                        "Peter Parker and his friends go on a summer trip to Europe. " +
                                "However, they will hardly be able to rest - Peter will have to agree " +
                                "to help Nick Fury uncover the mystery of creatures that cause natural " +
                                "disasters and destruction throughout the continent",
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 3
                )

                Spacer(Modifier.height(16.dp))

                /* BUTTONS */
                Row {
                    BannerButton(
                        text = "Play",
                        selected = selectedButton == 0
                    )

                    Spacer(Modifier.width(12.dp))

                    BannerButton(
                        text = "More Info",
                        selected = selectedButton == 1
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerButton(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (selected) Color.White else Color.DarkGray
            )
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

