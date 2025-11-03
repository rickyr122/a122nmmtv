package com.projects.a122mmtv.helper

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TvScaledBox(
    baseWidthPx: Int = 1280,
    content: @Composable (scale: Float) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
//        val scale = (maxWidth.value / baseWidthPx).coerceIn(0.9f, 1.5f)

        val scale = (maxWidth.value / baseWidthPx).coerceIn(0.6f, 1.2f)
        content(scale)
    }
}
