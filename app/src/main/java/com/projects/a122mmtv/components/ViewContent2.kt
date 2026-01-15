package com.projects.a122mmtv.components

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.MetaText
import com.projects.a122mmtv.helper.fixEncoding

@Composable
fun ViewContent2(
    modifier: Modifier = Modifier,
    horizontalInset: Dp,
    isActive: Boolean
) {

    // 🔥 MOCK DATA LIVES HERE
    var items by remember {
        mutableStateOf(
            listOf(
                PosterItem2(
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
                PosterItem2(
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
                PosterItem2(
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
                PosterItem2(
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
                PosterItem2(
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
                PosterItem2(
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

    val title = "Fresh From Theater"

    val heroHeight = 260.dp
    val heroWidth = heroHeight * (16f / 9f)
    var heroItem by remember { mutableStateOf(items.first()) }

    var isFirstFocused by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    var selectedItem by remember {
        mutableStateOf<PosterItem2?>(items.firstOrNull()) // 👈 id = 1 on first load
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = horizontalInset, top = 10.dp, bottom = 8.dp)
        //.border(2.5.dp, Color.Blue)
    ) {

        Text(
            text = "$title $isActive",
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
                        .padding(end = 6.dp)
                ) {
                    AsyncImage(
                        model = heroItem.posterUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            .border(1.dp, Color.White)
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
                            model = heroItem.logoUrl,
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
                    .fillMaxWidth(),

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
        }
    }
}

@Composable
private fun PosterCard(
    item: PosterItem2,
    modifier: Modifier = Modifier,
    isFirst: Boolean,
    isFirstFocused: Boolean
) {
    val showHero = isFirst && isFirstFocused

    Box(
        modifier = modifier
            .height(260.dp)
            .aspectRatio(9.5f / 16f)
            //.focusable()
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

private data class PosterItem2(
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

