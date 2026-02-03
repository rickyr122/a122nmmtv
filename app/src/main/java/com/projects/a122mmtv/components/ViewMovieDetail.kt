package com.projects.a122mmtv.components

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ActionButton(
    val id: String,
    val label: String,
    val iconRes: Int
)

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
            //.focusRequester(focusRequester)
            //.focusable()
    ) {
        if (isLoading || detail == null) return

        val movie = detail!!

        val getTitle = if (movie.m_title.isBlank()) movie.pTitle else movie.m_title

        val titleStarted = getTitle.replace(
            Regex("""S(\d+):E(\d+).*"""),
            "S$1: Ep.$2"
        )

        val titleNotStarted = getTitle.replace(
            Regex("""S(\d+):E(\d+).*"""),
            "Season $1: Episode $2"
        )

        val playCaption =
            if (movie.m_id.startsWith("MOV")) {
                if (movie.cProgress > 10) "Resume Playing" else "Play"
            } else {
                val title = if (movie.cProgress > 10) titleStarted else titleNotStarted
                "${if (movie.cProgress > 10) "Resume" else "Play"} $title"
            }



        val actionButtons = remember(movie) {
            buildList {

                add(
                    ActionButton(
                        id = "play",
                        label =  playCaption, //if (movie.c_remaining > 10) "Resume" else "Play",
                        iconRes = R.drawable.play
                    )
                )

                if (movie.cProgress > 10) {
                    add(
                        ActionButton(
                            id = "restart",
                            label = "Start from beginning",
                            iconRes = R.drawable.rollback
                        )
                    )
                }

                if (movie.gId.startsWith("TVG")) {
                    add(
                        ActionButton(
                            id = "episodes",
                            label = "More Episodes",
                            iconRes = R.drawable.episodes
                        )
                    )
                }

                add(
                    ActionButton(
                        id = "similar",
                        label = "More like this",
                        iconRes = R.drawable.collage
                    )
                )

                if (movie.cProgress > 0) {
                    add(
                        ActionButton(
                            id = "remove_continue",
                            label = "Remove from Continue Watching",
                            iconRes = R.drawable.close
                        )
                    )
                }

                add(
                    ActionButton(
                        id = "my_list",
                        label = if (movie.inList.toInt() == 0) "Add to My List" else "Remove from My List",
                        iconRes = if (movie.inList.toInt() == 0)
                            R.drawable.plus
                        else
                            R.drawable.close
                    )
                )
            }
        }

        val visibleButtons = actionButtons.take(3)

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
                            .heightIn(max = 70.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

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
                        Spacer(Modifier.width(4.dp))
                        //Bullets()
                        //Spacer(Modifier.width(2.dp))
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

                Spacer(Modifier.height(8.dp))

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

                Spacer(Modifier.height(8.dp))

                if (movie.m_title != "") {
                    Text(
                        text = movie.m_title.fixEncoding(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // DESCRIPTION (this now overlaps image)
                Text(
                    text = movie.m_description.fixEncoding(),
                    color = Color(0xFF91A3B0),
                    fontSize = 14.sp,
                    maxLines = 5
                )

                if (movie.c_remaining == 0) {
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
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFB3B3B3),
                                    fontSize = 12.sp
                                )
                            ) {
                                append(movie.m_starring.fixEncoding())
                            }
                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                    //Log.d("MovieDetail", "Director: '${movie.m_director.fixEncoding()}'")
                    val theMaster = if (movie.m_id.startsWith("MOV")) "Director: " else "Creator: "
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFB3B3B3),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(theMaster)
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFFB3B3B3),
                                    fontSize = 12.sp
                                )
                            ) {
                                append(movie.m_director.fixEncoding())
                            }
                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                }

                Spacer(Modifier.height(12.dp))

                // local state – ONLY for this block
                var selectedIndex by remember { mutableStateOf(0) }
                val collapseThumbs = selectedIndex >= 2

                val listState = rememberLazyListState()

                LaunchedEffect(selectedIndex) {
                    listState.animateScrollToItem(
                        index = maxOf(0, selectedIndex - 2)
                    )
                }
                val menuFocusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    menuFocusRequester.requestFocus()
                }

                Column(
                    modifier = Modifier
                        //.border(1.dp, Color.White)
                        .focusRequester(menuFocusRequester)
                        .focusable()
                        .onPreviewKeyEvent { event ->
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                            when (event.key) {
                                Key.DirectionDown -> {
                                    selectedIndex = (selectedIndex + 1).coerceAtMost(actionButtons.lastIndex)
                                    true
                                }
                                Key.DirectionUp -> {
                                    selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                                    true
                                }
                                else -> false
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    // ───────── THUMBS (same width as buttons) ─────────
                    AnimatedVisibility(visible = !collapseThumbs) {
                        Row(
                            modifier = Modifier
                                .width(300.dp)
                                .padding(start = 12.dp), // aligns with menu icon start
                            horizontalArrangement = Arrangement.spacedBy(32.dp), // 👈 fixed spacing
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            var hasRated by remember { mutableStateOf(movie.hasRated) }

                            Box( contentAlignment = Alignment.Center) {
                                MovieAction(
                                    icon = painterResource(
                                        if (hasRated == -5)
                                            R.drawable.ic_thumb_down_filled
                                        else
                                            R.drawable.ic_thumb_down
                                    ),
                                    label = "",
                                    iconType = "down"
                                ) {}
                            }

                            Box(contentAlignment = Alignment.Center) {
                                MovieAction(
                                    icon = painterResource(
                                        if (hasRated == 5)
                                            R.drawable.ic_thumb_up_filled
                                        else
                                            R.drawable.ic_thumb_up
                                    ),
                                    label = "",
                                    iconType = "up"
                                ) {}
                            }

                            Box(contentAlignment = Alignment.Center) {
                                MovieAction(
                                    icon = painterResource(
                                        if (hasRated == 10)
                                            R.drawable.ic_thumb_up_double_filled
                                        else
                                            R.drawable.ic_thumb_up_double
                                    ),
                                    label = "",
                                    iconType = "up_double"
                                ) {}
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    //var selectedIndex by remember { mutableIntStateOf(0) }
                    val buttonHeight = 44.dp
                    val buttonSpacing = 8.dp
                    val visibleCount = 3

                    val menuHeight =
                        buttonHeight * visibleCount +
                                buttonSpacing * (visibleCount - 1)

                    val selectedTextColor = Color.Black
                    val normalTextColor = Color.White.copy(alpha = 0.4f)
                    val fadedTextColor = Color.White.copy(alpha = 0.2f)

                    // ───────── SCROLLABLE MENU ─────────
                    val collapsedHeight =
                        menuHeight - ((menuHeight / 4) + 4.dp)

                    val expandedHeight = menuHeight - 8.dp

                    val currentMenuHeight =
                        if (selectedIndex >= 2) expandedHeight else collapsedHeight

//                    val progress = remember(movie.cProgress, movie.m_duration) {
//                        if (movie.m_duration > 0)
//                            (movie.cProgress / movie.m_duration.toFloat()).coerceIn(0f, 1f)
//                        else 0f
//                    }

                    val progress = (movie.c_percent?.toFloat() ?: 0f).coerceIn(0f, 1f)
                    val gap = 1.dp

                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .height(currentMenuHeight)   // 👈 THIS IS THE KEY
                            //.heightIn(max = 168.dp)
                            .clipToBounds()
                    ) {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(actionButtons) { index, item ->
                                val lastIndex = actionButtons.lastIndex

                                val textColor = when {
                                    // 1. Selected item: always solid
                                    selectedIndex == index ->
                                        selectedTextColor

                                    // 2. Initial state: fade the 3rd item
                                    selectedIndex == 0 && index == 2 ->
                                        fadedTextColor

                                    // 3. Reached bottom: fade topmost visible item
                                    selectedIndex >= 2 &&
                                            selectedIndex == lastIndex &&
                                            index == selectedIndex - 3 ->
                                        fadedTextColor

                                    // 4. Normal scrolling: fade item two above selection
                                    selectedIndex >= 2 &&
                                            selectedIndex != lastIndex &&
                                            index == selectedIndex - 2 ->
                                        fadedTextColor

//                                    selectedIndex >= 1 &&
//                                            index == selectedIndex + 1 ->
//                                        fadedTextColor

                                    // 5. Normal unselected
                                    else ->
                                        normalTextColor
                                }

                                SecondaryTextButton(
                                    isActive = selectedIndex == index,
                                    modifier = Modifier.width(300.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {

                                        // ICON
                                        Icon(
                                            painter = painterResource(item.iconRes),
                                            contentDescription = item.label,
                                            tint = textColor,
                                            modifier = Modifier.size(14.dp)
                                        )

                                        Spacer(Modifier.width(12.dp))

                                        // TEXT
                                        Text(
                                            text = item.label,
                                            color = textColor,
                                            fontSize = 14.sp,
                                            maxLines = 1
                                        )

                                        // PUSH EVERYTHING ELSE TO THE RIGHT
                                        Spacer(Modifier.weight(0.5f))

                                        // ───── PROGRESS BAR (ONLY FIRST BUTTON & cProgress >= 10) ─────
                                        if (index == 0 && selectedIndex == 0 && movie.cProgress >= 10) {

                                            Box(
                                                modifier = Modifier
                                                    .width(56.dp)        // 👈 small, subtle
                                                    .height(4.dp)
                                                    //.clip(RoundedCornerShape(2.dp))
                                                    .background(Color.Gray.copy(alpha = 0.25f))
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(progress)
                                                        .background(Color.Red)
                                                )
                                            }

                                        }
                                    }

                                }
                            }
                        }
                    }

                }

            }
        }
    }
}


@Composable
fun PrimaryButton(
    text: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .background(
                color = if (isActive) Color.White else Color.Black,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = if (isActive) Color.Black else Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SecondaryTextButton(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) Color.White else Color.Transparent,
                RoundedCornerShape(50)
            )
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        content()
    }
}

@Composable
fun MovieAction(
    icon: Painter,
    label: String,
    iconType: String = "", // "up", "up_double", "down"
    onClick: () -> Unit
) {
    var isTapped by remember { mutableStateOf(false) }

    // Animate scale (bounce) on tap
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = { isTapped = false } // reset when animation finishes
    )

    // Animate rotation (tilt) on tap
    val tiltAngle by animateFloatAsState(
        targetValue = if (isTapped) {
            when (iconType) {
                "up", "up_double", "down" -> -15f
                else -> 0f
            }
        } else 0f, // default back to 0
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
//            isTapped = true
//            onClick()
        }
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = Color.White, // always white
            modifier = Modifier
                .size(20.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = tiltAngle
                }
        )
//        Spacer(modifier = Modifier.height(6.dp))
//        Text(
//            text = label,
//            color = Color.White, // always white
//            style = MaterialTheme.typography.labelSmall
//        )
    }
}
