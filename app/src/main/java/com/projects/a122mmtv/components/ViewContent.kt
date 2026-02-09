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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class HomeMenuResponse(
    val title: String,
    val items: List<HomeMenuItem>
)

data class HomeMenuItem(
    val m_id: String,
    val m_title: String,
    val m_duration: String,
    val m_release_date: String,
    val m_year: String,
    val m_content: String,
    val mGenre: String,
    val mDescription: String,
    val cvrUrl: String,
    val bdropUrl: String,
    val logoUrl: String
)

private fun HomeMenuItem.toPosterItem(id: Int): PosterItem {
    return PosterItem(
        id = id,
        mId = m_id,
        posterUrl = bdropUrl.replace("/w1280","/w780").ifBlank { cvrUrl },
        logoUrl = logoUrl,
        mGenre = mGenre,
        mYear = m_year,
        mDuration = m_duration,
        mContent = m_content,
        mDescription = mDescription
    )
}

private data class PosterItem(
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



private fun rotateLeft(list: List<PosterItem>): List<PosterItem> {
    if (list.isEmpty()) return list
    return list.drop(1) + list.first()
}

private fun rotateRight(list: List<PosterItem>): List<PosterItem> {
    if (list.isEmpty()) return list
    return buildList {
        add(list.last())
        addAll(list.dropLast(1))
    }
}

@Composable
fun ViewContent(
    modifier: Modifier = Modifier,
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
    val context = LocalContext.current
    val api = ApiClient.create(AuthApiService::class.java)

    var title by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<PosterItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val heroHeight = 260.dp
    val heroWidth = heroHeight * (16f / 9f)
    //var heroItem by remember { mutableStateOf(items.first()) }
    var heroItem by remember { mutableStateOf<PosterItem?>(null) }

    val previewHeight = heroHeight
    val previewWidth = previewHeight * (6f / 19f)
    var isHeroActive by remember { mutableStateOf(false) }

    var hasActivatedOnce by remember { mutableStateOf(false) }

    var isFirstFocused by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    var selectedItem by remember {
        mutableStateOf<PosterItem?>(items.firstOrNull()) // 👈 id = 1 on first load
    }

    var previousHero by remember { mutableStateOf<PosterItem?>(null) }
//    var leftHero by remember { mutableStateOf<PosterItem?>(null) }

    //val firstItemFocusRequester = remember { FocusRequester() }
//    LaunchedEffect(items) {
//        if (heroItem == null && items.isNotEmpty()) {
//            heroItem = items.first()
//        }
//    }
//
    LaunchedEffect(items) {
        if (hasActivatedOnce) {
            listState.scrollToItem(0)
            //firstItemFocusRequester.requestFocus()
        }
    }

//    LaunchedEffect(items, isActive) {
//        // if (!isActive) return@LaunchedEffect
//        if (hasActivatedOnce) return@LaunchedEffect
//        if (items.isEmpty()) return@LaunchedEffect
//
//        // 🔥 EXACT old behavior
//        heroItem = items.first()
//        items = rotateLeft(items)
//        hasActivatedOnce = true
//    }


//    LaunchedEffect(items) {
//        if (hasActivatedOnce) return@LaunchedEffect
//        if (items.isEmpty()) return@LaunchedEffect
//
//        val first = items.first()
//        val rotated = rotateLeft(items)
//
//        // ✅ atomic state transition
//        heroItem = first
//        items = rotated
//        selectedItem = first
//        hasActivatedOnce = true
//    }


    val userId = homeSession.userId ?: 0
    LaunchedEffect(code) {
        isLoading = true

        try {
            //val userId = homeSession.userId ?: 0

            val res = api.getHomeMenu(
                code = code,
                page = 1,
                pageSize = 100,
                userId = userId
            )

            title = res.title   // ✅ FIX #1

            val mapped = res.items.mapIndexed { i: Int, item: HomeMenuItem ->
                item.toPosterItem(i)
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
                .clipToBounds()
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
                Box(
                    modifier = Modifier
                        .offset(x = horizontalInset)
                        .width(heroWidth)
                        .height(heroHeight)
                        //.padding(start = horizontalInset, end = 6.dp)
                        //.border(1.dp, Color.White)
                        .focusRequester(heroFocusRequester)
                        .focusable()
                        .alpha(0.8f)
                        .then(
                            if (isHeroActive) {
                                Modifier.border(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(Color.White, Color.LightGray)
                                    ),
                                    RoundedCornerShape(0.dp)
                                )
                            } else Modifier
                        )
                ) {
                    AsyncImage(
                        model = hero?.posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            //.border(1.dp, Color.White)
                    )

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
                            .align(BottomStart)
                            .padding(
                                start =  16.dp,
                                bottom = 12.dp
                            )
                    ) {
                        val maxLogoWidth =
                            maxWidth * 0.5f   // or whatever your normal size is

                        AsyncImage(
                            model = hero?.logoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .widthIn(max = maxLogoWidth)
                                .heightIn(max = 36.dp)
                        )
                    }
                }
            }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.8f)
                    .padding(start = if(!isActive) horizontalInset else horizontalInset + heroWidth + 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(
                    items = items,
                    key = { it.id }
                ) { item ->
                    val isFirst = item.id == items.first().id

                    PosterCard(
                        item = item,
                        isFirst = isFirst,
                        isFirstFocused = isFirstFocused
                    )
                }
            }
        }

        selectedItem?.let { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .background(Color.Black)
                    .padding(horizontal = horizontalInset, vertical = 12.dp)
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
    item: PosterItem,
    modifier: Modifier = Modifier,
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


