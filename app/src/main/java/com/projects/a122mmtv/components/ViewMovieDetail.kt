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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.projects.a122mmtv.R
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.dataclass.ApiClient
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText
import com.projects.a122mmtv.helper.convertContentRating
import com.projects.a122mmtv.helper.fixEncoding
import com.projects.a122mmtv.utility.formatDurationFromMinutes

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

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            /* =========================
             * RIGHT IMAGE (BACKGROUND LAYER)
             * ========================= */
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(0.6f)
                    .aspectRatio(16f / 9f)
                    .drawWithCache {
                        // (your existing mask code stays EXACTLY the same)
                        val edgeMask = Brush.radialGradient(
                            colors = listOf(Color.White, Color.White, Color.Transparent),
                            center = Offset(size.width * 0.65f, size.height * 0.45f),
                            radius = size.maxDimension * 1.2f
                        )

                        val leftMask = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.White),
                            startX = 0f,
                            endX = size.width * 0.6f
                        )

                        val bottomMask = Brush.verticalGradient(
                            colors = listOf(Color.White, Color.Transparent),
                            startY = size.height * 0.65f,
                            endY = size.height
                        )

                        onDrawWithContent {
                            drawContent()
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
                        .alpha(0.6f)
                )
            }

            /* =========================
             * LEFT TEXT (FOREGROUND LAYER)
             * ========================= */
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.55f)   // 👈 THIS controls overlap amount
                    .padding(horizontalInset)
                    .zIndex(1f)             // 👈 explicit foreground
            ) {

                // LOGO
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = movie.logoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart,
                        modifier = Modifier
                            .width(maxWidth * 0.8f)
                            .heightIn(max = 72.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // META
                val displayDuration = if (movie.m_id.startsWith("MOV")) {
                    formatDurationFromMinutes(movie.m_duration)
                } else if (movie.m_id.startsWith("TV")) {
                    if (movie.totalSeason == 1) {
                        "${movie.totalEps} Episodes"
                    } else {
                        "${movie.totalSeason} Seasons"
                    }
                } else {
                    "${movie.totalSeason} Seasons"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (movie.hasRated != 0) {
                        val ratIcon = when (movie.hasRated) {
                            -5  -> painterResource(R.drawable.ic_thumb_down_filled)
                            5  -> painterResource(R.drawable.ic_thumb_up_filled)
                            10  -> painterResource(R.drawable.ic_thumb_up_double_filled)
                            else -> painterResource(R.drawable.ic_thumb_up)
                        }

                        Icon(
                            painter = ratIcon,
                            contentDescription = "like status",
                            tint = Color.White, // always white
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Bullets()
                        Spacer(Modifier.width(2.dp))
                    }
                    MetaText(movie.m_year)
                    Spacer(Modifier.width(2.dp))
                    Bullets()
                    Spacer(Modifier.width(2.dp))
                    MetaText(movie.mGenre)
                    Spacer(Modifier.width(2.dp))
                    Bullets()
                    Spacer(Modifier.width(2.dp))
                    MetaText(displayDuration)
                    Spacer(Modifier.width(2.dp))
                    Bullets()
                    Spacer(Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF444444), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                    MetaText(movie.m_content.convertContentRating())
                        }
                }

                val rtState = movie.rt_state

                val rtIconRes = when {
                    rtState == "rotten" -> R.drawable.rotten
                    rtState == "fresh" -> R.drawable.fresh
                    else -> R.drawable.certifiedfresh
                }

                val rtAudience = movie.audience_state

                val rtAudienceRes = when {
                    rtAudience == "upright" -> R.drawable.upright
                    else -> R.drawable.spill
                }

                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.imdb),
                        contentDescription = "IMDb",
                        modifier = Modifier.size(22.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = movie.m_rating,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.width(12.dp))

                    Icon(
                        painter = painterResource(id = rtIconRes),
                        contentDescription = "Rotten Tomatoes",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = movie.rt_score.toString() + "%",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.width(8.dp))

                    if (rtAudienceRes != 0 && movie.audience_score != 0) {
                        Icon(
                            painter = painterResource(id = rtAudienceRes),
                            contentDescription = "Audience Score",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${movie.audience_score}%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                if (movie.m_title != "") {
                    Text(
                        text = movie.m_title.fixEncoding(),
                        color = Color(0xFF91A3B0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(10.dp))
                }

                // DESCRIPTION (this now overlaps image)
                Text(
                    text = movie.m_description.fixEncoding(),
                    color = Color.White,
                    fontSize = 14.sp,
                    maxLines = 5
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFFB3B3B3),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Cast: ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFFB3B3B3), fontSize = 12.sp)) {
                            append(movie.m_starring.fixEncoding())
                        }
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                if (movie.m_id.startsWith("MOV")) {
                    //Log.d("MovieDetail", "Director: '${movie.m_director.fixEncoding()}'")
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFB3B3B3),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Director: ")
                            }
                            withStyle(style = SpanStyle(color = Color(0xFFB3B3B3), fontSize = 12.sp)) {
                                append(movie.m_director.fixEncoding())
                            }
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(32.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryButton("Play", Modifier.width(200.dp))
                    SecondaryTextButton("More Episode")
                    SecondaryTextButton("Subtitles")
                }
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
