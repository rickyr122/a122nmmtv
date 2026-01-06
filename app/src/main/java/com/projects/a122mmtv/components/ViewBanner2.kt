package com.projects.a122mmtv.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import kotlinx.coroutines.android.awaitFrame

@Composable
fun ViewBanner2(
    focusRequester: FocusRequester,
    horizontalInset: Dp
) {
    var isFocused by remember { mutableStateOf(false) }
    var isOnPlay by remember { mutableStateOf(true) }
    var isOnInfo by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val buttonHeight = 36.dp

    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalInset)
            .focusRequester(focusRequester)
            .focusTarget() // 🔴 REQUIRED for TV focus
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.nativeKeyEvent.keyCode) {

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
                                Toast.makeText(
                                    context,
                                    "Play pressed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "More Info pressed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            true // 🔴 IMPORTANT: consume the event
                        }

                        else -> false
                    }
                } else false
            }

            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(0.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(21f / 9.5f)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w1280/4KF5Mgf74VfOVzeZPYO0V4Cp2IP.jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // ▶ PLAY
                Row(
                    modifier = Modifier
                        .height(buttonHeight)
                        .background(
                            color = if (isOnPlay) Color.White else Color(0xFF3A3A3A),
                            shape = RoundedCornerShape(50)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play",
                        color = if (isOnPlay) Color.Black else Color.White
                    )
                }

                // ℹ MORE INFO
                Row(
                    modifier = Modifier
                        .height(buttonHeight) // ✅ same height
                        .background(
                            color = if (isOnInfo) Color.White else Color(0xFF3A3A3A),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "More Info",
                        color = if (isOnInfo) Color.Black else Color.White
                    )
                }
            }



        }
    }
}

