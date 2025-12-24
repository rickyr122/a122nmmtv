package com.projects.a122mmtv.components

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
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
    var isFocused by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
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
                if (isFocused) {
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
                .aspectRatio(21f / 10f)
        ) {
            val contentWidth = maxWidth * 0.3f
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w1280/Bwh7Lol5k3hSqYOtqXWxbbJVMx.jpg",
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
                    model = "https://image.tmdb.org/t/p/w500/dEFxlY8tBr3MfcofIJKzqQKVe6B.png",
                    contentDescription = "title logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(72.dp)
                        .aspectRatio(3.5f)
                )


                Spacer(modifier = Modifier.height(12.dp))

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
                    visible = isFocused,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Peter Parker is unmasked and no longer able to separate his normal life from the high-stakes of being a super-hero.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Buttons
                AnimatedVisibility(
                    visible = isFocused,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        BannerButton(
                            text = "Play",
                            icon = "▶"
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
        fontSize = 10.sp,
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
    text: String,
    icon: String? = null,

) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .padding(horizontal = 20.dp, vertical = 8.dp)
    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                //Spacer(Modifier.width(6.dp))
                Text(
                    icon,
                    // color = fg,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

        }
    }
}


