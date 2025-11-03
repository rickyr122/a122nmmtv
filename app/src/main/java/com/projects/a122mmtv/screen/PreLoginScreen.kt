package com.projects.a122mmtv.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.projects.a122mmtv.R
import com.projects.a122mmtv.helper.TvScaledBox

@Composable
fun PreLoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    // Background first (not scaled), then scale all inner paddings/sizes
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TvScaledBox { s ->
            // Center the content block and scale paddings
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 96.dp * s, vertical = 64.dp * s),
                contentAlignment = Alignment.Center
            ) {
                // Banner image (already includes logo+text)
                Image(
                    painter = painterResource(id = R.drawable.pre_login_banner),
                    contentDescription = "Pre-login banner",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight(0.98f)         // keep the big, cinematic look
                        .aspectRatio(16f / 9f)
                )

                // Red Sign In button (scaled smaller on 1080p, larger on 4K)
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.98f)
                        .aspectRatio(16f / 9f),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // --- Better TV button sizing: never too small ---
                    val btnMinW = 160.dp
                    val btnMinH = 48.dp
                    val btnW = (140.dp * s).coerceAtLeast(btnMinW)
                    val btnH = (44.dp * s).coerceAtLeast(btnMinH)
                    //val btnTextSize = if ((18.sp * s).value < 16.sp.value) 16.sp else 18.sp * s
                    val btnTextSize = if ((22.sp * s).value < 18.sp.value) 18.sp else 22.sp * s
                    val btnHPad = (20.dp * s).coerceAtLeast(16.dp)
                    val btnVPad = (10.dp * s).coerceAtLeast(8.dp)

                    Button(
                        onClick = { navController.navigate("login") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(6.dp * s),
                        modifier = Modifier
                            .padding(32.dp * s)
                            .width(btnW)
                            .height(btnH),
                        contentPadding = PaddingValues(horizontal = btnHPad, vertical = btnVPad)
                    ) {
                        Text(
                            "Sign In",
                            color = Color.White,
                            fontSize = btnTextSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
