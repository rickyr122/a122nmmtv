package com.projects.a122mmtv.screen

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainPlayerScreen(
    mId: String,
    isActive: Boolean,
    onClose: () -> Unit
) {
    if (!isActive) return

    // 🔙 System BACK
    BackHandler(enabled = true) {
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // 🔥 full screen overlay
            .border(1.dp, Color.White)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_BACK -> {
                        onClose()
                        true
                    }
                    else -> true // 🔥 block ALL DPAD while player active
                }
            }
    ) {

        // 🔝 TOP CONTENT (like player header)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)

        ) {
            Text(
                text = "mId: $mId",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // (future)
        // Player surface goes here (ExoPlayer / SurfaceView)
    }
}
