package com.projects.a122mmtv.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.focusable
import com.projects.a122mmtv.helper.TvScaledBox // from your file

@Composable
fun LoginScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val phoneReq = remember { FocusRequester() }
    val remoteReq = remember { FocusRequester() }

    LaunchedEffect(Unit) { phoneReq.requestFocus() }

    TvScaledBox { s ->
        val scale = s * 0.9f // slightly smaller for 1080p

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding((32f * scale).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                "Choose how to sign in",
                color = Color.White,
                fontSize = (36f * scale).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height((24f * scale).dp))

            // Tabs
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabButton(
                    text = "Use Phone",
                    isSelected = selectedTab == 0,
                    modifier = Modifier
                        .focusRequester(phoneReq)
                        .focusProperties { right = remoteReq }
                        .onFocusChanged { if (it.isFocused) selectedTab = 0 }
                        .focusable()
                )
                Spacer(Modifier.width((16f * scale).dp))
                TabButton(
                    text = "Use Remote",
                    isSelected = selectedTab == 1,
                    modifier = Modifier
                        .focusRequester(remoteReq)
                        .focusProperties { left = phoneReq }
                        .onFocusChanged { if (it.isFocused) selectedTab = 1 }
                        .focusable()
                )
            }

            // ✅ More space between tabs and content
            Spacer(Modifier.height((72f * scale).dp))

            if (selectedTab == 0) PhonePage(scale) else RemotePage(scale)
        }
    }
}

/* ---------- Page 1 : Use Phone ---------- */
@Composable
private fun PhonePage(scale: Float) {
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        // Left section
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            // ✅ Number centered in circle, text top-aligned to circle top
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    Modifier
                        .size((36f * scale).dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF3A3A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("1", color = Color.White, fontSize = (18f * scale).sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width((12f * scale).dp))
                Text(
                    "Use your phone or tablet’s camera\nand point to the code:",
                    color = Color.White,
                    fontSize = (22f * scale).sp,
                    lineHeight = (28f * scale).sp,
                    modifier = Modifier
                        .padding(top = (2f * scale).dp) // small nudge upward so top aligns with circle
                )
            }

            Spacer(Modifier.height((24f * scale).dp))

            // ✅ QR box aligned left with the text
            Box(
                Modifier
                    .padding(start = (48f * scale).dp) // matches number+space offset
                    .size((300 * scale).dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("QR", color = Color.White, fontSize = (48f * scale).sp)
            }
        }

        Spacer(Modifier.width((40f * scale).dp))

        // Right section
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier
                        .size((36f * scale).dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF3A3A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("2", color = Color.White, fontSize = (18f * scale).sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width((12f * scale).dp))
                Text(
                    "Confirm the code on your phone or tablet",
                    color = Color.White,
                    fontSize = (22f * scale).sp,
                    lineHeight = (28f * scale).sp,
                    modifier = Modifier
                        .padding(top = (2f * scale).dp) // same top offset for alignment
                )
            }

            Spacer(Modifier.height((16f * scale).dp))

            // ✅ Code aligned left with "Confirm..." text
            Text(
                "1 2 3 4 - 5 6 7 8",
                color = Color.White,
                fontSize = (56f * scale).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = (48f * scale).dp)
                    .align(Alignment.Start)
            )
        }
    }
}

/* ---------- Page 2 : Use Remote ---------- */
@Composable
private fun RemotePage(scale: Float) {
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .weight(1f)
                .height((280 * scale).dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Keyboard", color = Color.White, fontSize = (22f * scale).sp)
        }

        Spacer(Modifier.width((36f * scale).dp))

        Column(
            Modifier
                .weight(1f)
                .padding(top = (8f * scale).dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            LabeledField(label = "Email address", scale = scale, placeholderOnly = true)
            Spacer(Modifier.height((16f * scale).dp))
            LabeledField(label = "Password", isPassword = true, scale = scale, placeholderOnly = true)
        }
    }
}


@Composable
private fun LabeledField(
    label: String,
    isPassword: Boolean = false,
    scale: Float,
    placeholderOnly: Boolean = false
) {
    var value by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(6.dp)

    Box(
        Modifier
            .fillMaxWidth()
            .height((52 * scale).dp)
            .clip(shape)
            .background(Color(0xFF4A4A4A))
            .border(1.dp, Color(0xFF777777), shape)
            .padding(horizontal = (14 * scale).dp, vertical = (8 * scale).dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = { value = it },
            textStyle = TextStyle(color = Color.White, fontSize = (18 * scale).sp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth().focusable(),
            decorationBox = { innerTextField ->
                if (value.isEmpty() && placeholderOnly) {
                    Text(
                        text = label,
                        color = Color(0xFFBDBDBD),
                        fontSize = (18 * scale).sp
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color.White else Color(0xFF4A4A4A)
    val fg = if (isSelected) Color.Black else Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = fg, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}
