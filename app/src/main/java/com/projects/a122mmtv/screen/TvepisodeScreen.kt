package com.projects.a122mmtv.screen

import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.AuthApiService.TvSeasonCountResponse
import com.projects.a122mmtv.dataclass.ApiClient
import com.projects.a122mmtv.helper.Bullets
import com.projects.a122mmtv.helper.BulletsContinue
import com.projects.a122mmtv.helper.MetaText
import com.projects.a122mmtv.helper.MetaTextContinue
import kotlinx.coroutines.android.awaitFrame

@Composable
fun TvEpisodeScreen(
    mId: String,
    isActive: Boolean,
    onClose: () -> Unit
) {
    if (!isActive) return

    BackHandler(enabled = isActive) {
        onClose()
    }

    val api = remember {
        ApiClient.create(AuthApiService::class.java)
    }

    var data by remember { mutableStateOf<TvSeasonCountResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 🔥 Load API
    LaunchedEffect(mId) {
        isLoading = true

        try {
            data = api.getTvSeasonCount(code = mId)
        } catch (e: Exception) {
            data = null
        }

        isLoading = false
    }

    val leftFocusRequester = remember { FocusRequester() }
    val rightFocusRequester = remember { FocusRequester() }

    //var focusOnRight by remember { mutableStateOf(true) }
    var leftFocused by remember { mutableStateOf(false) }
    var rightFocused by remember { mutableStateOf(false) }


    LaunchedEffect(isActive, isLoading) {
        if (isActive && !isLoading) {
            awaitFrame()
            rightFocusRequester.requestFocus()
        }
    }

    var selectedSeasonIndex by remember { mutableStateOf(0) }
    val seasonListState = rememberLazyListState()

    LaunchedEffect(selectedSeasonIndex) {
        val firstVisibleIndex =
            (selectedSeasonIndex - 5).coerceAtLeast(0)
        seasonListState.animateScrollToItem(firstVisibleIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                when (event.nativeKeyEvent.keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        //leftFocused = true
                        leftFocusRequester.requestFocus()
                        true
                    }

                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        //rightFocused = true
                        rightFocusRequester.requestFocus()
                        true
                    }

//                    KeyEvent.KEYCODE_BACK -> {
//                        onClose()
//                        true
//                    }

                    else -> false
                }
            }
    ) {

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.Red,
                    strokeWidth = 4.dp
                )
            }
            return@Box
        }


        val tv = data ?: return@Box

        Row(Modifier.fillMaxSize()) {

            /* =========================
               LEFT PANEL
            ========================= */
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .focusRequester(leftFocusRequester)
                    .onFocusChanged { leftFocused = it.isFocused }
                    .focusable()
                    .onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                        when (event.nativeKeyEvent.keyCode) {

                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                selectedSeasonIndex =
                                    (selectedSeasonIndex + 1).coerceAtMost(tv.seasons.lastIndex)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_UP -> {
                                selectedSeasonIndex =
                                    (selectedSeasonIndex - 1).coerceAtLeast(0)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                rightFocusRequester.requestFocus()
                                true
                            }

                            else -> false
                        }
                    }

                    .border(
                        width = if (leftFocused) 3.dp else 0.dp,
                        color = Color.White
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp)
                ) {

                    AsyncImage(
                        model = tv.logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()          // 👈 take full width
                            .heightIn(max = 50.dp),
                        alignment = Alignment.CenterStart   // 👈 force left
                    )

                    Spacer(Modifier.height(12.dp))

                    val prefixSeason = if(tv.total_season.toInt() > 1) "Seasons" else "Season"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MetaText(tv.m_year)
                        Bullets()
                        MetaText("${tv.total_season} $prefixSeason")
                    }

                    Spacer(Modifier.height(32.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = seasonListState,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(tv.seasons) { index, season ->

                            val isSelected = index == selectedSeasonIndex
                            val isFocused = leftFocused

                            val backgroundColor = when {
                                isFocused && isSelected -> Color.White
                                isSelected -> Color.DarkGray
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isFocused && isSelected -> Color.Black
                                else -> Color.White
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor, RoundedCornerShape(50))
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Season ${season.season}",
                                    color = textColor,
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = "${season.episodes} episodes",
                                    color = textColor,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            /* =========================
               RIGHT PANEL
            ========================= */
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .focusRequester(rightFocusRequester)
                    .onFocusChanged { rightFocused  = it.isFocused }
                    .focusable()
                    .border(
                        width = if (rightFocused) 3.dp else 0.dp,
                        color = Color.White
                    )
            ) {
                // future episode list
            }
        }
    }
}