package com.projects.a122mmtv.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.dataclass.ApiClient
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText

@Composable
fun ViewMovieDetail(
    mId: String,
    isActive: Boolean,
    homeSession: HomeSessionViewModel,
    horizontalInset: Dp,
    onClose: () -> Unit
) {
    if (!isActive) return

    BackHandler { onClose() }

    val api = remember {
        ApiClient.create(AuthApiService::class.java)
    }

    var detail by remember { mutableStateOf<AuthApiService.MovieDetailDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive, mId) {
        if (!isActive) return@LaunchedEffect

        focusRequester.requestFocus()
        isLoading = true

        try {
            val userId = homeSession.userId ?: 0
            val resp = api.getMovieDetail(mId, userId)
            detail = if (resp.isSuccessful) resp.body() else null
        } catch (_: Exception) {
            detail = null
        }

        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
    ) {
        if (isLoading || detail == null) return

        val movie = detail!!

        /* =========================
         * TWO COLUMNS
         * ========================= */
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            /* =========================
             * LEFT COLUMN (TEXT)
             * uses horizontalInset
             * ========================= */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontalInset                    )
            ) {

                // LOGO
                AsyncImage(
                    model = movie.logoUrl,
                    contentDescription = null,
                    modifier = Modifier.height(72.dp)
                )

                Spacer(Modifier.height(24.dp))

                // META
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MetaText(movie.m_year)
                    Bullets()
                    MetaText(movie.m_rating)
                    Bullets()
                    MetaText("${movie.m_duration}h")
                    Bullets()
                    MetaText(movie.m_content)
                }

                Spacer(Modifier.height(16.dp))

                // DESCRIPTION
                Text(
                    text = movie.m_description,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 5
                )

                Spacer(Modifier.height(32.dp))

                // BUTTONS (VERTICAL)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        text = "Play",
                        modifier = Modifier.width(200.dp)
                    )

                    SecondaryTextButton("More Episode")
                    SecondaryTextButton("Subtitles")
                }
            }

            /* =========================
             * RIGHT COLUMN (IMAGE)
             * NO horizontalInset
             * ========================= */
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .aspectRatio(16f / 9f)
                    .drawWithCache {

                        // Alpha mask: white = visible, black = transparent
                        val edgeMask = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color.White,
                                Color.Transparent
                            ),
                            center = Offset(
                                x = size.width * 0.65f,
                                y = size.height * 0.45f
                            ),
                            radius = size.maxDimension * 1.2f
                        )

                        val leftMask = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White
                            ),
                            startX = 0f,
                            endX = size.width * 0.8f
                        )

                        val bottomMask = Brush.verticalGradient(
                            colors = listOf(
                                Color.White,
                                Color.Transparent
                            ),
                            startY = size.height * 0.65f,
                            endY = size.height
                        )

                        onDrawWithContent {
                            drawContent()

                            // Apply feathered alpha masks
                            drawRect(edgeMask, blendMode = BlendMode.DstIn)
                            drawRect(leftMask, blendMode = BlendMode.DstIn)
                            drawRect(bottomMask, blendMode = BlendMode.DstIn)
                        }
                    }
            ) {

                AsyncImage(
                    model = movie.bdropUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(0.6f) // base transparency
                )
            }

        }
    }
}


@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
            .focusable(),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Black, fontSize = 14.sp)
    }
}

@Composable
fun SecondaryTextButton(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        modifier = Modifier
            .focusable()
            .padding(vertical = 6.dp)
    )
}
