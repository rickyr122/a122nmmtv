package com.projects.a122mmtv.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.projects.a122mmtv.R
import com.projects.a122mmtv.helper.TvScaledBox
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TvSplashOverlay(
    startExit: Boolean,
    onExitFinished: () -> Unit
) {
    TvScaledBox { s ->

        // Size & position
        val startSize = 150.dp * s
        val startOffsetY = 0.dp
        val endSize = (96.dp * s).coerceAtLeast(72.dp)
        val endOffsetY = ((-260).dp * s)

        val sizeAnim = remember { Animatable(startSize, Dp.VectorConverter) }
        val offsetAnim = remember { Animatable(startOffsetY, Dp.VectorConverter) }

        // 🔹 Fade-IN only
        val alphaAnim = remember { Animatable(0f) }

        // 🔹 Run fade-in ONCE
        LaunchedEffect(Unit) {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(350)
            )
        }

        // 🔹 Exit motion (no fade-out)
        LaunchedEffect(startExit) {
            if (startExit) {
                coroutineScope {
                    launch {
                        sizeAnim.animateTo(
                            endSize,
                            animationSpec = tween(700)
                        )
                    }
                    launch {
                        offsetAnim.animateTo(
                            endOffsetY,
                            animationSpec = tween(700)
                        )
                    }
                }
                onExitFinished()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .graphicsLayer { alpha = alphaAnim.value },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.a122mm_logo),
                contentDescription = null,
                modifier = Modifier
                    .offset(y = offsetAnim.value)
                    .size(sizeAnim.value)
            )
        }
    }
}