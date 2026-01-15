package com.projects.a122mmtv.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewContent2(
    modifier: Modifier = Modifier,
    horizontalInset: Dp,
) {

    val title = "Fresh From Theater"
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = horizontalInset, top = 10.dp, bottom = 8.dp)
            .background(Color(0xFF1E1E1E))
    ) {

        // 🔹 TITLE AREA (own column)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp
            )
        }

        // 🔹 CONTENT AREA
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF333333))
        )
    }
}

