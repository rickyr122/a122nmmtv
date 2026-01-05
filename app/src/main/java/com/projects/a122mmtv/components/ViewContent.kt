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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private data class PosterItem(
    val id: Int,
    val posterUrl: String,
    val logoUrl: String
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
    horizontalInset: Dp
) {
    // 🔥 MOCK DATA LIVES HERE
    var items by remember {
        mutableStateOf(
            listOf(
                PosterItem(
                    1,
                    "https://image.tmdb.org/t/p/w1280/9tOkjBEiiGcaClgJFtwocStZvIT.jpg",
                    "https://image.tmdb.org/t/p/w500/gNkaNY2Cg2BvYunWVgMVcbmQgc5.png"
                ),
                PosterItem(
                    2,
                    "https://image.tmdb.org/t/p/w1280/xPNDRM50a58uvv1il2GVZrtWjkZ.jpg",
                    "https://image.tmdb.org/t/p/w500/7yXEfWFDGpqIfq9wdpMOHcHbi8g.png"
                ),
                PosterItem(
                    3,
                    "https://image.tmdb.org/t/p/w1280/cKvDv2LpwVEqbdXWoQl4XgGN6le.jpg",
                    "https://image.tmdb.org/t/p/w500/f1EpI3C6wd1iv7dCxNi3vU5DAX7.png"
                ),
                PosterItem(
                    4,
                    "https://image.tmdb.org/t/p/w1280/fm6KqXpk3M2HVveHwCrBSSBaO0V.jpg",
                    "https://image.tmdb.org/t/p/w500/b07VisHvZb0WzUpA8VB77wfMXwg.png"
                ),
                PosterItem(
                    5,
                    "https://image.tmdb.org/t/p/w1280/ufpeVEM64uZHPpzzeiDNIAdaeOD.jpg",
                    "https://image.tmdb.org/t/p/w500/xJMMxfKD9WJQLxq03p7T0c2AWb4.png"
                ),
                PosterItem(
                    6,
                    "https://image.tmdb.org/t/p/w1280/34jW8LvjRplM8Pv06cBFDpLlenR.jpg",
                    "https://image.tmdb.org/t/p/w500/lAEaCmWwqkZSp8lAw6F7CfPkA9N.png"
                )
            )
        )
    }

    var isFirstFocused by remember { mutableStateOf(false) }
    var rotationTick by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()


    // ✅ MUST live here (top-level of the composable)
    LaunchedEffect(rotationTick) {
        listState.scrollToItem(0)
        firstItemFocusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalInset, top = 10.dp, bottom = 8.dp)
            .alpha(if (isFirstFocused) 1f else 0.45f)
    ) {

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .onPreviewKeyEvent { event ->
                    if (
                        event.type == KeyEventType.KeyDown &&
                        event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT &&
                        isFirstFocused
                    ) {
                        items = rotateLeft(items)
                        rotationTick++
                        true
                    } else false
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                    isFirstFocused = it.isFocused
                                }
                                .onPreviewKeyEvent { event ->
                                    if (
                                        event.type == KeyEventType.KeyDown &&
                                        event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP
                                    ) {
                                        onRequestShowBanner()
                                        true
                                    } else false
                                }
                        } else {
                            if (isFirstFocused) Modifier.padding(start = 2.dp)
                            else Modifier
                        }
                    )
                )
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
            //.height(if (showHero) 160.dp else 225.dp)   // 👈 HEIGHT FIRST
            .height(240.dp)
            .aspectRatio(if (showHero) 16f / 9f else 2f / 3f)
            //.clip(RoundedCornerShape(6.dp))
            .then(
                if (showHero) Modifier.border(0.5.dp, Color.White)
                else Modifier
            )
            .focusable()
    )
    {

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

    }
}


