package com.projects.a122mmtv.components

import android.view.KeyEvent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.dataclass.ApiClient
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText
import com.projects.a122mmtv.helper.convertContentRating
import com.projects.a122mmtv.helper.fixEncoding
import com.projects.a122mmtv.pages.InteractionLayer
import com.projects.a122mmtv.utility.formatDurationFromMinutes

// --- API MODELS (LOCAL) ---

data class TopContentResponse(
    val title: String,
    val items: List<TopContentItem>
)

data class TopContentItem(
    val mId: String,
    val m_title: String,
    val m_duration: String,
    val m_year: String,
    val m_content: String,
    val mGenre: String,
    val mDescription: String,
    val cvrUrl: String,
    val bdropUrl: String,
    val logoUrl: String
)

private fun TopContentItem.toTopPosterItem(id: Int): TopPosterItem {
    return TopPosterItem(
        id = id,
        mId = mId,
        posterUrl = bdropUrl.replace("/w1280","/w780").ifBlank { cvrUrl },
        logoUrl = logoUrl,
        mGenre = mGenre,
        mYear = m_year,
        mDuration = m_duration,
        mContent = m_content,
        mDescription = mDescription
    )
}

private data class TopPosterItem(
    val id: Int,
    val mId: String,
    val posterUrl: String,
    val logoUrl: String,
    val mGenre: String,
    val mYear: String,
    val mDuration: String,
    val mContent: String,
    val mDescription: String
)



private fun rotateLeft(list: List<TopPosterItem>): List<TopPosterItem> {
    if (list.isEmpty()) return list
    return list.drop(1) + list.first()
}

private fun rotateRight(list: List<TopPosterItem>): List<TopPosterItem> {
    if (list.isEmpty()) return list
    return buildList {
        add(list.last())
        addAll(list.dropLast(1))
    }
}

@Composable
fun ViewTopContent(
    modifier: Modifier = Modifier,
    type: String,
    horizontalInset: Dp,
    homeSession: HomeSessionViewModel,
    code: Int,
    isActive: Boolean,
    focusRequester: FocusRequester,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onExitToMenu: () -> Unit,
    onOpenDetail: (String) -> Unit,
    heroFocusRequester: FocusRequester,
    interactionLayer: InteractionLayer
) {
    //val title = "Fresh From Theater"

    val context = LocalContext.current
    val api = ApiClient.create(AuthApiService::class.java)

    //var title by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<TopPosterItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val heroHeight = 260.dp
    val heroWidth = heroHeight * (16f / 9f)
    //var heroItem by remember { mutableStateOf(items.first()) }
    var heroItem by remember { mutableStateOf<TopPosterItem?>(null) }

    val previewHeight = heroHeight
    val previewWidth = previewHeight * (6f / 19f)
    var isHeroActive by remember { mutableStateOf(false) }

    var hasActivatedOnce by remember { mutableStateOf(false) }

    var isFirstFocused by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    var selectedItem by remember {
        mutableStateOf<TopPosterItem?>(items.firstOrNull()) // 👈 id = 1 on first load
    }

    val MAX_VISIBLE_ITEMS = 9

    var previousHero by remember { mutableStateOf<TopPosterItem?>(null) }
//    var leftHero by remember { mutableStateOf<TopPosterItem?>(null) }

    LaunchedEffect(items) {
        if (hasActivatedOnce) {
            listState.scrollToItem(0)
            //firstItemFocusRequester.requestFocus()
        }
    }

    val userId = homeSession.userId ?: 0
    LaunchedEffect(code) {
        isLoading = true

        try {
            //val userId = homeSession.userId ?: 0

            val res = api.getTopContent(
                type = type
            )

            val mapped = res.mapIndexed { i, item ->
                item.toTopPosterItem(i)
            }

            if (mapped.isNotEmpty()) {
                heroItem = mapped.first()
                items = mapped // if (isActive) rotateLeft(mapped) else mapped //rotateLeft(mapped)
                selectedItem = heroItem
            } else {
                items = emptyList()
                heroItem = null
            }

        } catch (e: Exception) {
            items = emptyList()
            heroItem = null
        }

        hasActivatedOnce = true
        isLoading = false
    }

    var wasActive by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive && !wasActive && items.isNotEmpty()) {
            items = rotateLeft(items)
        }

        if (!isActive && wasActive && items.isNotEmpty()) {
            // restore original order
            items = rotateRight(items)
        }

        wasActive = isActive
    }

    var stepIndex by remember { mutableStateOf(0) }
    val visibleCount = remember(stepIndex) {
        (MAX_VISIBLE_ITEMS - stepIndex).coerceAtLeast(0)
    }

    val visibleItems = remember(items, visibleCount) {
        items.take(minOf(visibleCount, items.size))
    }

    val title = when (type) {
        "HOM" -> "Top 10 TV Shows and Movies"
        "MOV" -> "Top 10 Movies"
        "TVG" -> "Top 10 TV Shows"
        else  -> "Top 10"
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 0.dp, top = 10.dp, bottom = 8.dp)
        //.border(2.5.dp, Color.Blue)
    ) {
        //val userId = homeSession.userId
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(start = horizontalInset, bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.clipToBounds()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isHeroActive = it.hasFocus
                }
                .focusable()
                .onPreviewKeyEvent { event ->
                    if (!isActive) return@onPreviewKeyEvent false
                    if (interactionLayer != InteractionLayer.HOME) return@onPreviewKeyEvent false
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false


                    when (event.nativeKeyEvent.keyCode) {

                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
//                            leftHero = previousHero
//                            previousHero = heroItem
//                            Log.d("heroItem::check", "heroItem DR -> $heroItem")
//                            Log.d("leftHero::check", "LeftHero DR -> $leftHero")
                            if (stepIndex >= 9) {
                                // 🚫 already at start → block LEFT
                                return@onPreviewKeyEvent true
                            }

                            stepIndex += 1


                            items = rotateLeft(items)
                            heroItem = items.last()
                            previousHero = items.getOrNull(items.lastIndex - 1)

                            selectedItem = heroItem
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (stepIndex == 0) {
                                // 🚫 already at start → block LEFT
                                return@onPreviewKeyEvent true
                            }

//                            previousHero = leftHero //heroItem
//                            Log.d("heroItem::check", "heroItem DL -> $heroItem")
//                            Log.d("leftHero::check", "LeftHero DL -> $leftHero")
                            stepIndex -= 1

                            // Step 1: rotate right
                            items = rotateRight(items)

                            // Step 2: hero becomes LAST item (not first!)
                            heroItem = items.last()
                            previousHero = items.getOrNull(items.lastIndex - 1)

                            selectedItem = heroItem

                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            rotateLeft(items)
                            onMoveDown()   // 🔥 delegate to parent

                            true
                        }

                        KeyEvent.KEYCODE_DPAD_UP -> {
                            onMoveUp()     // 🔥 delegate to parent
                            true
                        }

                        KeyEvent.KEYCODE_BACK -> {
                            onExitToMenu()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER -> {
                            heroItem?.mId?.let { onOpenDetail(it) }
                            true
                        }

                        else -> false
                    }
                }
        ) {
            key(stepIndex) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = stepIndex > 0 && previousHero != null,
                    enter = fadeIn() + slideInHorizontally { it / 2 },
                    exit = fadeOut() + slideOutHorizontally { it / 2 }
                ) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = horizontalInset - previewWidth - 6.dp
                            )
                            .width(previewWidth)
                            .height(previewHeight)
                            //.padding(end = horizontalInset - 6.dp)
                            .alpha(0.45f)
                    ) {
                        AsyncImage(
                            model = previousHero?.posterUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            val hero = heroItem

            // HERO (only when active)
            if (isActive) {
                RankedPosterTV(
                    rank = stepIndex + 1,
                    posterUrl = heroItem!!.posterUrl,
                    logoUrl = heroItem!!.logoUrl,
                    cardHeight = heroHeight,
                    cardWidth = heroWidth,
                    numberSpace = 72.dp,
                    overlap = 24.dp,
                    showBorder = if (isHeroActive) true else false,
                    centerLogo = false,
                    modifier = Modifier
                        .offset(x = horizontalInset)
                        .focusRequester(heroFocusRequester)
                        .focusable()
                        .alpha(0.8f)
                )
            }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.8f)
                    .padding(
                        start = if (!isActive) {
                            horizontalInset
                        } else {
                            horizontalInset +
                                    72.dp +
                                    heroWidth -
                                    24.dp +
                                    28.dp
                        }
                    ),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                itemsIndexed(visibleItems)
                { index, item ->
                    //val isFirst = item.id == items.first().id
                    val isFirst = item.id == visibleItems.first().id
                    val displayRank = if (!isActive) {
                        index + 1
                    } else {
                        stepIndex + index + 2
                    }

                    RankedPosterTV(
                        rank = displayRank,
                        posterUrl = item.posterUrl,
                        logoUrl = item.logoUrl,
                        cardHeight = 260.dp,
                        cardWidth = 260.dp * (9.5f / 16f),
                        numberSpace = 56.dp,
                        overlap = 20.dp,
                        centerLogo = true
                    )

                }
            }
        }

        selectedItem?.let { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .background(Color.Black)
                    .padding(
                        start = horizontalInset + 48.dp, // 👈 MATCH hero numberSpace
                        end = horizontalInset,
                        top = 12.dp,
                        bottom = 12.dp
                    )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val sType = if (item.mId.startsWith("MOV")) "Movie" else "Shows"
                    val sDuration = if (item.mId.startsWith("MOV")) formatDurationFromMinutes(item.mDuration.toIntOrNull() ?: 0) else item.mDuration
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetaText(sType)
                        Bullets()
                        MetaText(item.mGenre)
                        Bullets()
                        MetaText(item.mYear)
                        Bullets()
                        MetaText(sDuration)
                        Bullets()
                        MetaText(item.mContent.convertContentRating())
                    }

                    Text(
                        modifier = Modifier.alpha(0.8f),
                        text = item.mDescription.fixEncoding(),
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 4
                    )
                }
            }
        }
    }
}

@Composable
private fun PosterCard(
    item: TopPosterItem,
    modifier: Modifier = Modifier,
    rank: Int,
    isFirst: Boolean,
    isFirstFocused: Boolean
) {
    val showHero = isFirst && isFirstFocused

    Box(
        modifier = modifier
            .height(260.dp)
            .aspectRatio(9.5f / 16f)
            .focusable()
    ) {
        AsyncImage(
            model = item.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

//        RankNumber(
//            rank = rank,
//            modifier = Modifier
//                .align(Alignment.CenterStart)
//                .width(56.dp)
//                .zIndex(1f)
//        )

        // bottom gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier
                .align(BottomCenter)
                .padding(
                    //start = if (showHero) 16.dp else 0.dp,
                    bottom = 12.dp
                )
        ) {
            val maxLogoWidth =
                maxWidth * 0.8f   // or whatever your normal size is

            AsyncImage(
                model = item.logoUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .widthIn(max = maxLogoWidth)
                    .heightIn(max = 36.dp)
            )
        }
    }
}

@Composable
fun RankNumber(
    rank: Int,
    containerHeight: Dp, // 👈 ADD THIS
    modifier: Modifier = Modifier
) {
    val text = rank.toString()

    // Pre-measure glyphs (reuse your mobile logic)
    val density = LocalDensity.current
   // val fontSize = containerHeight * 0.4f // 72.sp
    val fontSizeSp = with(density) {
        (containerHeight * 0.5f).toSp()
    }

    val strokePx = with(density) { 6.dp.toPx() }
    val isMultiDigit = text.length > 1

    val trackingFactor =
        if (isMultiDigit) 0.5f   // 👈 overlap digits (for 10)
        else 0.92f               // normal spacing (1–9)

    val measurer = rememberTextMeasurer()

    val glyphs = remember(text) {
        text.map { char ->
            val outline = measurer.measure(
                AnnotatedString(char.toString()),
                style = TextStyle(fontSize = fontSizeSp)
            )
            val fill = measurer.measure(
                AnnotatedString(char.toString()),
                style = TextStyle(fontSize = fontSizeSp * 0.92f)
            )
            outline to fill
        }
    }

    val combinedOutlineWidthPx =
        glyphs.sumOf { it.first.size.width }.toFloat() * trackingFactor


    val shiftLeftPx =
        if (isMultiDigit) combinedOutlineWidthPx * 0.15f else 0f

    Box(
        modifier = modifier
            .fillMaxHeight()
            .drawBehind {
                val maxGlyphHeight =
                    glyphs.maxOfOrNull { it.first.size.height } ?: 0

                val startX =
                    (size.width - combinedOutlineWidthPx) / 2f - shiftLeftPx
//                    (size.width - combinedOutlineWidthPx) / 2f
                val startY =
                    (size.height - maxGlyphHeight) / 2f

                var cursorX = startX

                glyphs.forEachIndexed { index, (outline, fill) ->
                    // OUTLINE
                    drawText(
                        textLayoutResult = outline,
                        topLeft = Offset(cursorX, startY),
                        color = Color.White.copy(alpha = 0.8f),
                        drawStyle = Stroke(
                            width = strokePx,
                            join = StrokeJoin.Round,
                            cap = StrokeCap.Round
                        )
                    )

                    // FILL
                    val fillX =
                        cursorX + (outline.size.width - fill.size.width) / 2f
                    val fillY =
                        startY + (outline.size.height - fill.size.height) / 2f

                    drawText(
                        textLayoutResult = fill,
                        topLeft = Offset(fillX, fillY),
                        color = Color.Black.copy(alpha = 0.8f)
                    )

                    val advance =
                        if (index < glyphs.lastIndex)
                            outline.size.width * trackingFactor
                        else
                            outline.size.width.toFloat()

                    cursorX += advance
                }
            }
    )
}

@Composable
private fun RankedPosterTV(
    modifier: Modifier = Modifier,
    rank: Int,
    posterUrl: String,
    logoUrl: String?,
    cardHeight: Dp,
    cardWidth: Dp,
    numberSpace: Dp,
    showBorder: Boolean = false,
    centerLogo: Boolean = false,
    overlap: Dp
) {
    val totalWidth = numberSpace + cardWidth

    Box(
        modifier = modifier
            .height(cardHeight)
            .width(totalWidth)
    ) {

        // LEFT: rank number (IDENTICAL to mobile)
        RankNumber(
            rank = rank,
            containerHeight = cardHeight, // 👈 THIS IS THE KEY
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(numberSpace)
                .fillMaxHeight(0.5f)
        )

        // Gradient between image & rank
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // RIGHT: poster overlaps number
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = -overlap)
                .width(cardWidth)
                .height(cardHeight)
                .then(
                    if (showBorder)
                        Modifier.border(1.dp, Color.White)
                    else
                        Modifier
                )
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // bottom gradient (same as your current code)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // logo (optional)
            if (logoUrl != null) {
                BoxWithConstraints(
                    modifier = Modifier
                        .align(
                            if (centerLogo)
                                BottomCenter
                            else
                                BottomStart
                        )
                        .padding(start = if (centerLogo) 0.dp else 16.dp,
                            bottom = 12.dp)
                ) {
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .widthIn(
                                max = if (centerLogo)
                                    maxWidth * 0.8f   // LazyRow
                                else
                                    maxWidth * 0.6f   // Hero (unchanged)
                            )
                            .heightIn(
                                max = if (centerLogo)
                                    36.dp             // LazyRow
                                else
                                    36.dp             // Hero (same as before)
                            )
                    )
                }
            }
        }
    }
}
