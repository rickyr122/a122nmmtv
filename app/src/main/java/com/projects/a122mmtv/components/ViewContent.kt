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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private data class PosterItem(
    val id: Int,
    val posterUrl: String,
    val logoUrl: String
)

@Composable
fun ViewContent(
    title: String = "Fresh from Theater",
    firstItemFocusRequester: FocusRequester,
    onRequestShowBanner: () -> Unit
) {
    // 🔥 MOCK DATA LIVES HERE
    val mockItems = listOf(
        PosterItem(
            1,
            "https://image.tmdb.org/t/p/w1280/ufpeVEM64uZHPpzzeiDNIAdaeOD.jpg",
            "https://image.tmdb.org/t/p/w500/xJMMxfKD9WJQLxq03p7T0c2AWb4.png"
        ),
        PosterItem(
            2,
            "https://image.tmdb.org/t/p/w1280/xPNDRM50a58uvv1il2GVZrtWjkZ.jpg",
            "https://image.tmdb.org/t/p/w500/7yXEfWFDGpqIfq9wdpMOHcHbi8g.png"
        ),
        PosterItem(
            3,
            "https://image.tmdb.org/t/p/w1280/fm6KqXpk3M2HVveHwCrBSSBaO0V.jpg",
            "https://image.tmdb.org/t/p/w500/b07VisHvZb0WzUpA8VB77wfMXwg.png"
        ),
        PosterItem(
            4,
            "https://image.tmdb.org/t/p/w1280/34jW8LvjRplM8Pv06cBFDpLlenR.jpg",
            "https://image.tmdb.org/t/p/w500/lAEaCmWwqkZSp8lAw6F7CfPkA9N.png"
        ),
        PosterItem(
            5,
            "https://image.tmdb.org/t/p/w1280/cKvDv2LpwVEqbdXWoQl4XgGN6le.jpg",
            "https://image.tmdb.org/t/p/w500/f1EpI3C6wd1iv7dCxNi3vU5DAX7.png"
        ),
        PosterItem(
            6,
            "https://image.tmdb.org/t/p/w1280/9tOkjBEiiGcaClgJFtwocStZvIT.jpg",
            "https://image.tmdb.org/t/p/w500/gNkaNY2Cg2BvYunWVgMVcbmQgc5.png"
        )
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 8.dp)
            .alpha(0.45f)
    ) {

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(mockItems) { index, item ->
                PosterCard(
                    item = item,
                    modifier = Modifier
                        .then(
                            if (index == 0) Modifier
                                .focusRequester(firstItemFocusRequester)
                                .onPreviewKeyEvent { event ->
                                    if (
                                        event.type == KeyEventType.KeyDown &&
                                        event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP
                                    ) {
                                        onRequestShowBanner()
                                        true
                                    } else false
                                }
                            else Modifier
                        )
                )
            }
        }

    }
}

@Composable
private fun PosterCard(
    item: PosterItem,
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(150.dp)
            .aspectRatio(2f / 3f)
            .focusable()
    ) {

        // 🔹 Poster image
        AsyncImage(
            model = item.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        )

        // 🔹 Bottom gradient (optional but recommended)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // 🔹 Title logo
        AsyncImage(
            model = item.logoUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .height(36.dp)        // 👈 controls logo size
                .fillMaxWidth(0.8f)  // 👈 keep margins left/right
        )
    }
}

