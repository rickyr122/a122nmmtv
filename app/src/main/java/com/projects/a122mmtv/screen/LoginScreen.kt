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

    TvScaledBox { scale ->
        val adjustedScale = scale * 1.3f // slightly enlarged for better visibility

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding((32 * adjustedScale).dp)
                .graphicsLayer(scaleX = adjustedScale, scaleY = adjustedScale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Choose how to sign in",
                color = Color.White,
                fontSize = (36 * adjustedScale).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height((24 * adjustedScale).dp))

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
                        .onFocusChanged { s -> if (s.isFocused) selectedTab = 0 }
                        .focusable()
                )
                Spacer(Modifier.width((16 * adjustedScale).dp))
                TabButton(
                    text = "Use Remote",
                    isSelected = selectedTab == 1,
                    modifier = Modifier
                        .focusRequester(remoteReq)
                        .focusProperties { left = phoneReq }
                        .onFocusChanged { s -> if (s.isFocused) selectedTab = 1 }
                        .focusable()
                )
            }

            Spacer(Modifier.height((32 * adjustedScale).dp))

            if (selectedTab == 0) PhonePage(adjustedScale) else RemotePage(adjustedScale)
        }
    }
}

/* ---------- Tab Button ---------- */
@Composable
private fun TabButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    val bg = if (isSelected) Color.White else Color(0xFF4A4A4A)
    val fg = if (isSelected) Color.Black else Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text, color = fg, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

/* ---------- Page 1 : Use Phone ---------- */
@Composable
private fun PhonePage(scale: Float) {
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            // Align everything to left edge
            NumberedStep(
                step = "1",
                text = "Use your phone or tablet’s camera\nand point to the code:",
                scale = scale
            )

            Spacer(Modifier.height((24 * scale).dp))

            // QR + Code aligned left under the text, not under the circle
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    Modifier
                        .size((320 * scale).dp)
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("QR", color = Color.White, fontSize = (48 * scale).sp)
                }

                Spacer(Modifier.height((24 * scale).dp))

                Text(
                    "1 2 3 4 - 5 6 7 8",
                    color = Color.White,
                    fontSize = (56 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width((40 * scale).dp))

        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            NumberedStep(step = "2", text = "Confirm the code on your phone or tablet", scale = scale)
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
            Text("Keyboard", color = Color.White, fontSize = (22 * scale).sp)
        }

        Spacer(Modifier.width((36 * scale).dp))

        // Align top-right
        Column(
            Modifier
                .weight(1f)
                .padding(top = (8 * scale).dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
            LabeledField(label = "Email address", scale = scale, placeholderOnly = true)
            Spacer(Modifier.height((16 * scale).dp))
            LabeledField(label = "Password", isPassword = true, scale = scale, placeholderOnly = true)
        }
    }
}

/* ---------- Shared Helpers ---------- */
@Composable
private fun NumberedStep(step: String, text: String, scale: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size((36 * scale).dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF3A3A3A)),
            contentAlignment = Alignment.Center
        ) {
            Text(step, color = Color.White, fontSize = (18 * scale).sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width((12 * scale).dp))
        Text(
            text,
            color = Color.White,
            fontSize = (22 * scale).sp,
            lineHeight = (28 * scale).sp
        )
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