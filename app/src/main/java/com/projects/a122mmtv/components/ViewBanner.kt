package com.projects.a122mmtv.components

import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ViewBanner(
    navController: NavController,
    type: String,
    currentTabIndex: Int,
    focusRequester: FocusRequester,
    upMenuFocusRequester: FocusRequester,
    onBannerFocused: () -> Unit
) {
   // var isFocused by remember { mutableStateOf(false) }
    var isBannerActive by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                isBannerActive = it.hasFocus   // 👈 key change
                if (it.isFocused) onBannerFocused()
            }

            .focusable()
            .onPreviewKeyEvent { event ->
                if (
                    event.type == KeyEventType.KeyDown &&
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP
                ) {
                    upMenuFocusRequester.requestFocus()
                    true
                } else {
                    false
                }
            }
            .focusProperties {
                exit = {
                    when (it) {
                        FocusDirection.Up -> {
                            upMenuFocusRequester.requestFocus()
                            FocusRequester.Cancel
                        }
                        else -> FocusRequester.Default
                    }
                }
            }
            .then(
                if (isBannerActive) {
                    Modifier.border(
                        width = 0.5.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White, Color.LightGray)
                        ),
                        shape = shape
                    )
                } else Modifier
            )
            .clip(shape)
    ) {

        // ────────────────────
        // Banner background
        // ────────────────────
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(21f / 9f)
        ) {
            val contentWidth = maxWidth * 0.35f
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w1280/9tOkjBEiiGcaClgJFtwocStZvIT.jpg",
                contentDescription = "banner",
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // ────────────────────
            // Bottom-left overlay
            // ────────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 28.dp)
                    .width(contentWidth),
                horizontalAlignment = Alignment.Start
            ) {

                // Title logo (proportional)
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500/gNkaNY2Cg2BvYunWVgMVcbmQgc5.png",
                    contentDescription = "title logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(72.dp)
                        .wrapContentWidth(Alignment.Start) // 👈 KEY
                )


                Spacer(modifier = Modifier.height(4.dp))

                // Meta info row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MetaText("Movie")
                    Bullet()
                    MetaText("Superhero")
                    Bullet()
                    MetaText("2021")
                    Bullet()
                    MetaText("2h 3m")
                    Bullet()
                    MetaText("PG-13")
                }

                Spacer(Modifier.height(8.dp))

                AnimatedVisibility(
                    visible = isBannerActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Determined to prove herself, Officer Judy Hopps, the first bunny on Zootopia's police force, jumps at the chance to crack her first case - even if it means partnering with scam-artist fox Nick Wilde to solve the mystery.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        //maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Buttons
                AnimatedVisibility(
                    visible = isBannerActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val playFocusRequester = remember { FocusRequester() }

                    LaunchedEffect(isBannerActive) {
                        if (isBannerActive) {
                            playFocusRequester.requestFocus()
                        }
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {

                        BannerButton(
                            modifier = Modifier.focusRequester(playFocusRequester),
                            text = "Play",
                            icon = Icons.Filled.PlayArrow
                        )

                        Spacer(Modifier.width(12.dp))

                        BannerButton(
                            text = "More Info"
                        )


                    }
                }

            }
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 8.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.alpha(0.9f)
    )
}

@Composable
private fun Bullet() {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(2.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.7f))
    )
}

@Composable
private fun BannerButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null
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
                if (
                    event.type == KeyEventType.KeyDown &&
                    (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                            event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    Toast.makeText(
                        context,
                        "You click $text button",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                } else false
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
