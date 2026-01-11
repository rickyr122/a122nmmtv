package com.projects.a122mmtv.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Bullet
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText
import com.projects.a122mmtv.helper.fixEncoding

private data class PosterItem(
    val id: Int,
    val posterUrl: String,
    val logoUrl: String,
    val mType: String,
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


@Composable
fun ViewContent(
    title: String = "Fresh from Theater",
    firstItemFocusRequester: FocusRequester,
    onRequestShowBanner: () -> Unit,
    onRequestFocusSelf: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    horizontalInset: Dp,
    sectionIndex: Int,
    isActiveRow: Boolean,
    onRowFocused: () -> Unit,
    activeRowIndex: Int
) {
    // 🔥 MOCK DATA LIVES HERE
    var items by remember {
        mutableStateOf(
            listOf(
                PosterItem(
                    1,
                    "https://image.tmdb.org/t/p/w1280/34jW8LvjRplM8Pv06cBFDpLlenR.jpg",
                    "https://image.tmdb.org/t/p/w500/lAEaCmWwqkZSp8lAw6F7CfPkA9N.png",
                    "Movie",
                    "Action",
                    "2019",
                    "2h 9m",
                    "13+",
                    "Peter Parker and his friends go on a summer trip to Europe. However, they will hardly be able to rest" +
                            " - Peter will have to agree to help Nick Fury uncover the mystery of creatures that cause " +
                            "natural disasters and destruction throughout the continent."
                ),
                PosterItem(
                    2,
                    "https://image.tmdb.org/t/p/w1280/xPNDRM50a58uvv1il2GVZrtWjkZ.jpg",
                    "https://image.tmdb.org/t/p/w500/7yXEfWFDGpqIfq9wdpMOHcHbi8g.png",
                    "Movie",
                    "Action",
                    "2025",
                    "2h 50m",
                    "18+",
                    "Ethan Hunt and team continue their search for the terrifying AI known as the Entity — " +
                            "which has infiltrated intelligence networks all over the globe — with the world's governments " +
                            "and a mysterious ghost from Hunt's past on their trail."
                ),
                PosterItem(
                    3,
                    "https://image.tmdb.org/t/p/w1280/cKvDv2LpwVEqbdXWoQl4XgGN6le.jpg",
                    "https://image.tmdb.org/t/p/w500/f1EpI3C6wd1iv7dCxNi3vU5DAX7.png",
                    "Movie",
                    "Action",
                    "2008",
                    "2h 6m",
                    "13+",
                    "After being held captive in an Afghan cave, billionaire engineer Tony Stark creates a unique " +
                            "weaponized suit of armor to fight evil."
                ),
                PosterItem(
                    4,
                    "https://image.tmdb.org/t/p/w1280/fm6KqXpk3M2HVveHwCrBSSBaO0V.jpg",
                    "https://image.tmdb.org/t/p/w500/b07VisHvZb0WzUpA8VB77wfMXwg.png",
                    "Movie",
                    "Drama",
                    "2023",
                    "3h 1m",
                    "18+",
                    "The story of J. Robert Oppenheimer's role in the development of the atomic bomb during World War II."
                ),
                PosterItem(
                    5,
                    "https://image.tmdb.org/t/p/w1280/ufpeVEM64uZHPpzzeiDNIAdaeOD.jpg",
                    "https://image.tmdb.org/t/p/w500/xJMMxfKD9WJQLxq03p7T0c2AWb4.png",
                    "Movie",
                    "Action",
                    "2024",
                    "2h 8m",
                    "18+",
                    "A listless Wade Wilson toils away in civilian life with his days as the morally flexible mercenary, Deadpool, behind him. " +
                            "But when his homeworld faces an existential threat, Wade must reluctantly suit-up again " +
                            "with an even more reluctant Wolverine."
                ),
                PosterItem(
                    6,
                    "https://image.tmdb.org/t/p/w1280/9tOkjBEiiGcaClgJFtwocStZvIT.jpg",
                    "https://image.tmdb.org/t/p/w500/gNkaNY2Cg2BvYunWVgMVcbmQgc5.png",
                    "Movie",
                    "Animation",
                    "2016",
                    "1h 49m",
                    "7+",
                    "Determined to prove herself, Officer Judy Hopps, the first bunny on Zootopia's police force, " +
                            "jumps at the chance to crack her first case - even if it means partnering " +
                            "with scam-artist fox Nick Wilde to solve the mystery."
                )

            )
        )
    }

    var isFirstFocused by remember { mutableStateOf(false) }
    var rotationTick by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    var focusedItem by remember { mutableStateOf<PosterItem?>(null) }
    //val isFocusableSection = sectionIndex == 0

    val isAboveActive = sectionIndex < activeRowIndex
    val isActive = sectionIndex == activeRowIndex
    val isBelowActive = sectionIndex > activeRowIndex

    val heroHeight = 260.dp
    val heroWidth = heroHeight * (16f / 9f)
    var heroItem by remember { mutableStateOf(items.first()) }
    var hasActivatedOnce by remember { mutableStateOf(false) }


    // ✅ MUST live here (top-level of the composable)
    LaunchedEffect(items) {
        if (hasActivatedOnce) {
            listState.scrollToItem(0)
            firstItemFocusRequester.requestFocus()
        }
    }


    LaunchedEffect(isActive) {
        if (isActive && !hasActivatedOnce) {
            heroItem = items.first()
            items = rotateLeft(items)

            // mark AFTER rotation
            hasActivatedOnce = true
        }

        if (!isActive && heroItem != null) {
            items = buildList {
                add(heroItem)
                addAll(items.filter { it.id != heroItem.id })
            }
            hasActivatedOnce = false
        }

    }

    var selectedItem by remember {
        mutableStateOf<PosterItem?>(items.firstOrNull()) // 👈 id = 1 on first load
    }

    if (isAboveActive) {
        return
    }

    val rowAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.45f,
        animationSpec = tween(220)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalInset, top = 10.dp, bottom = 8.dp)
            //.alpha(if (isFirstFocused) 1f else 0.45f)
            .alpha(rowAlpha)
            //.border(2.5.dp, Color.Blue)
    ) {

        Text(
            text = "$title $sectionIndex $isActive",
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
        ) {
            // HERO (only when active)
            if (isActive) {
                Box(
                    modifier = Modifier
                        .width(heroWidth)
                        .height(heroHeight)
                        .padding(end = 12.dp)
                ) {
                    AsyncImage(
                        model = heroItem.posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                                    .border(1.dp, Color.White)
                    )
                }
            }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth(),
//                    .onPreviewKeyEvent { event ->
//                        if (
//                            event.type == KeyEventType.KeyDown &&
//                            event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT &&
//                            isFirstFocused
//                        ) {
//                            items = rotateLeft(items)
//                            heroItem = items.last()   // 👈 the promoted item
//                            selectedItem = heroItem
//                            true
//                        } else false
//                    },
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
                        isFirstFocused = isFirstFocused,
                        modifier = Modifier.then(
                            if (isFirst) {
                                Modifier
                                    .focusRequester(firstItemFocusRequester)
                                    .onFocusChanged {
                                        if (it.isFocused) {
                                            isFirstFocused = true
                                            onRowFocused()
                                        } else {
                                            isFirstFocused = false
                                        }
                                    }
                                    .onPreviewKeyEvent { event ->
                                        if (event.type == KeyEventType.KeyDown) {
                                            when (event.nativeKeyEvent.keyCode) {
                                                KeyEvent.KEYCODE_DPAD_DOWN -> {
                                                    onMoveDown(); true
                                                }

                                                KeyEvent.KEYCODE_DPAD_UP -> {
                                                    if (sectionIndex == 0) {
                                                        onMoveUp()
                                                        false   // 🔥 LET FOCUS SYSTEM MOVE TO BANNER
                                                    } else {
                                                        onMoveUp()
                                                        true
                                                    }
                                                }

                                                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                                    items = rotateLeft(items)
                                                    heroItem = items.last()
                                                    selectedItem = heroItem
                                                    true
                                                }


                                                else -> false
                                            }
                                        } else false
                                    }
                            } else Modifier
                        )
                    )
                }
            }
        }

        selectedItem?.let { item ->
//            AnimatedVisibility(
//                visible = isActive,
//                enter = fadeIn(), //+ expandVertically(),
//                exit = fadeOut() //+ shrinkVertically()
//            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .background(Color.Black)
                        .padding(vertical = 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MetaText(item.mType)
                            Bullets()
                            MetaText(item.mGenre)
                            Bullets()
                            MetaText(item.mYear)
                            Bullets()
                            MetaText(item.mDuration)
                            Bullets()
                            MetaText(item.mContent)
                        }

                        Text(
                            text = item.mDescription.fixEncoding(),
                            color = Color.White,
                            fontSize = 14.sp,
                            maxLines = 4
                        )
                    }
                }
           // }

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
            //.height(if (showHero) 160.dp else 225.dp)   // 👈 HEIGHT FIRST
            .height(260.dp)
            .aspectRatio(9.5f / 16f)
            //.aspectRatio(if (showHero) 16f / 9.5f else 9.5f / 16f)
            //.clip(RoundedCornerShape(6.dp))
//            .then(
//                if (showHero) Modifier.border(0.5.dp, Color.White)
//                else Modifier
//            )
            .focusable()
    )
    {

        AsyncImage(
            model = item.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        val showMeta = isFirst && isFirstFocused

        // bottom gradient
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

        BoxWithConstraints(
            modifier = Modifier
                .align(if (showHero) Alignment.BottomStart else Alignment.BottomCenter)
                .padding(
                    start = if (showHero) 16.dp else 0.dp,
                    bottom = 12.dp
                )
        ) {
            val maxLogoWidth =
                if (showHero) maxWidth * 0.5f
                else maxWidth * 0.8f   // or whatever your normal size is

            AsyncImage(
                model = item.logoUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .widthIn(max = maxLogoWidth)
                    .heightIn(max = if (showHero) 48.dp else 36.dp)
            )
        }

//        if (showMeta) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(300.dp)
//                    .background(Color.Black)
//                    .padding(horizontal = 12.dp, vertical = 10.dp)
//            ) {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    MetaText(item.mType)
//                    Bullets()
//                    MetaText(item.mGenre)
//                    Bullets()
//                    MetaText(item.mYear)
//                    Bullets()
//                    MetaText(item.mDuration)
//                    Bullets()
//                    MetaText(item.mContent)
//                }
//            }
//        }
    }
}


