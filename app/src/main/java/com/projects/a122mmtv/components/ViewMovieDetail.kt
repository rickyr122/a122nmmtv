package com.projects.a122mmtv.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewMovieDetail(
    mId: String,
    isActive: Boolean,
    onClose: () -> Unit
) {

    if (!isActive) return

    BackHandler(enabled = isActive) {
        onClose()
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .border(
                width = 2.dp,
                color = if (isActive) Color.Blue else Color.Red
            )
            .focusRequester(focusRequester)
            .focusable()
    ) {
        Text(
            text = "Movie Detail: $mId",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}