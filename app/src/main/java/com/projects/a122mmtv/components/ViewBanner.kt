package com.projects.a122mmtv.components

import android.view.KeyEvent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.dp
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
                if (it.isFocused) {
                    onBannerFocused()
                }
            }

            .focusable()
            .onPreviewKeyEvent { event ->
                if (
                    event.type == KeyEventType.KeyDown &&
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP
                ) {
                    upMenuFocusRequester.requestFocus()
                    true // consume event, stop spatial focus
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

            //.padding(horizontal = 24.dp)
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White,
                                Color.LightGray
                            )
                        ),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w1280/Bwh7Lol5k3hSqYOtqXWxbbJVMx.jpg",
            contentDescription = "banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )
    }
}

