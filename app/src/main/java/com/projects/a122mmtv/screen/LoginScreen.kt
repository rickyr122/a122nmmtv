package com.projects.a122mmtv.screen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.Image
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import android.view.KeyEvent
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.projects.a122mmtv.auth.AuthRepository
import com.projects.a122mmtv.auth.LoginViewModel
import com.projects.a122mmtv.auth.LoginUiState
import com.projects.a122mmtv.auth.TokenStore
import com.projects.a122mmtv.dataclass.AuthNetwork
import com.projects.a122mmtv.R
import com.projects.a122mmtv.auth.AuthApiService
import com.projects.a122mmtv.auth.HomeSessionViewModel
import com.projects.a122mmtv.getDeviceId
import com.projects.a122mmtv.helper.TvScaledBox // from your file
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeSession: HomeSessionViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val phoneReq = remember { FocusRequester() }
    val remoteReq = remember { FocusRequester() }

    val context = LocalContext.current

    val repo = remember {
        AuthRepository(
            publicApi = AuthNetwork.publicAuthApi,
            authedApi = AuthNetwork.authedAuthApi(context),
            store = TokenStore(context)
        )
    }

    val vm = remember { LoginViewModel(repo) }
    val ui = vm.ui.collectAsState().value

    LaunchedEffect(Unit) { phoneReq.requestFocus() }

    TvScaledBox { s ->
        val scale = s * 0.9f // slightly smaller for 1080p
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.login_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

//            // Optional dark overlay (recommended for readability)
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black.copy(alpha = 0.25f))
//            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                // Tabs container
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFF4A4A4A), RoundedCornerShape(50)) // capsule background
                        .padding(vertical = (3f * scale).dp, horizontal = (4f * scale).dp) // smaller overall height
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TabButton(
                            text = "Use Phone",
                            isSelected = selectedTab == 0,
                            scale = scale,
                            modifier = Modifier
                                .focusRequester(phoneReq)
                                .focusProperties { right = remoteReq }
                                .onFocusChanged { if (it.isFocused) selectedTab = 0 }
                                .focusable()
                        )
                        TabButton(
                            text = "Use Remote",
                            isSelected = selectedTab == 1,
                            scale = scale,
                            modifier = Modifier
                                .focusRequester(remoteReq)
                                .focusProperties { left = phoneReq }
                                .onFocusChanged { if (it.isFocused) selectedTab = 1 }
                                .focusable()
                        )
                    }
                }


                // ✅ More space between tabs and content
                Spacer(Modifier.height((72f * scale).dp))

                if (selectedTab == 0) {
                    PhonePage(
                        scale,
                        repo,
                        navController,
                        homeSession = homeSession
                    )
                } else {
                    RemotePage(
                        scale = scale,
                        vm = vm,
                        ui = ui,
                        navController = navController
                    )
                }

            }
        }
    }
}

/* ---------- Page 1 : Use Phone ---------- */
@Composable
private fun PhonePage(
    scale: Float,
    repo: AuthRepository,
    navController: NavHostController,
    homeSession: HomeSessionViewModel
) {
    val context = LocalContext.current
    val tokenStore = remember { TokenStore(context) }
    var reloadKey by remember { mutableStateOf(0) }

    // Pairing state
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    var pairCode by remember { mutableStateOf("") }
    var pollToken by remember { mutableStateOf("") }
    var verifyUrl by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Call tv_pair_start when this page is shown
    LaunchedEffect(reloadKey) {
        isLoading = true
        error = null
        try {
            val deviceId = getDeviceId(context)

            repo.tvPairStart(deviceId)
                .onSuccess { body ->
                    pairCode = body.pair_code
                    pollToken = body.poll_token
                    verifyUrl = body.verify_url

                    qrBitmap = generateQrBitmap(
                        text = verifyUrl,
                        sizePx = (900 * scale).toInt().coerceAtLeast(400)
                    )
                }
                .onFailure {
                    error = it.message ?: "Failed to generate QR"
                }
        } finally {
            isLoading = false
        }
    }

    // 2) Poll status while we have a pollToken
    LaunchedEffect(pollToken) {
        if (pollToken.isBlank()) return@LaunchedEffect

        val deviceId = getDeviceId(context)
        val deviceName = android.os.Build.MODEL ?: "Android TV"

        while (true) {
            // poll every 2 seconds
            kotlinx.coroutines.delay(2000)

            val res = repo.tvPairStatus(deviceId, pollToken, deviceName)
            res.onSuccess { st ->
                when (st.status.uppercase()) {
                    "PENDING" -> Unit
                    "EXPIRED" -> {
                        error = "Code expired. Please try again."
                        pairCode = "" // clear the number
                        pollToken = "" // stop polling
                        qrBitmap = null // clearQR
                        return@LaunchedEffect
                    }
                    "APPROVED" -> {
                        homeSession.setUser(
                            id = st.user_id ?:0,
                            name = st.username ?:"",
                            pplink = st.pp_link ?: ""
                        )
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                        pollToken = ""
                        return@LaunchedEffect
                    }


                    else -> Unit
                }
            }.onFailure {
                // Don’t kill the flow for temporary network issues
                // Just keep polling.
            }
        }
    }

    Row(
        Modifier
            .fillMaxSize()
            .padding(start = (40f * scale).dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left section
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
                    Text("1", color = Color.White, fontSize = (18f * scale).sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width((12f * scale).dp))
                Text(
                    "Open My Room menu in the app, tap the scan icon and point to the code.",
                    color = Color.White,
                    fontSize = (22f * scale).sp,
                    lineHeight = (28f * scale).sp,
                    modifier = Modifier.padding(top = (2f * scale).dp)
                )
            }

            Spacer(Modifier.height((24f * scale).dp))

            // QR BOX
            Box(
                Modifier
                    .padding(start = (48f * scale).dp)
                    .size((300 * scale).dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        Text("Loading...", color = Color.White, fontSize = (18f * scale).sp)
                    }
                    error != null -> {
                        var refreshFocused by remember { mutableStateOf(false) }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error!!,
                                color = Color.White,
                                fontSize = (16f * scale).sp
                            )

                            Spacer(Modifier.height((16f * scale).dp))

                            Box(
                                modifier = Modifier
                                    .size((48f * scale).dp)
                                    .onFocusChanged { refreshFocused = it.isFocused }
                                    .focusable() // ✅ TV remote focus
                                    .onKeyEvent {
                                        if (it.type == KeyEventType.KeyUp &&
                                            (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER ||
                                                    it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                                        ) {
                                            reloadKey++
                                            error = null
                                            true
                                        } else {
                                            false
                                        }
                                    }

                                    .clip(CircleShape)
                                    .background(
                                        if (refreshFocused) Color.White else Color(0x66FFFFFF)
                                    )
                                    .scale(if (refreshFocused) 1.1f else 1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Refresh,
                                    contentDescription = "Reload",
                                    tint = if (refreshFocused) Color.Black else Color.White,
                                    modifier = Modifier.size((26f * scale).dp)
                                )
                            }
                        }
                    }

                    qrBitmap != null -> {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize().padding((10f * scale).dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    else -> {
                        Text("QR", color = Color.White, fontSize = (48f * scale).sp)
                    }
                }
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
                    "Confirm the code on the app",
                    color = Color.White,
                    fontSize = (22f * scale).sp,
                    lineHeight = (28f * scale).sp,
                    modifier = Modifier.padding(top = (2f * scale).dp)
                )
            }

            Spacer(Modifier.height((16f * scale).dp))

            // REAL pairing code (replaces mock 1234-5678)
            Text(
                text = if (isLoading) "---- ----" else if (pairCode.isBlank()) "-- -- - -- --" else pairCode,
                color = Color.White,
                fontSize = (56f * scale).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = (48f * scale).dp)
                    .align(Alignment.Start)
            )

            // Optional: small helper text (you can remove)
            if (!isLoading && error == null && pairCode.isNotBlank()) {
                Spacer(Modifier.height((10f * scale).dp))
                Text(
                    text = "Tap Sign in to TV from the app to pair this device.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = (16f * scale).sp,
                    modifier = Modifier.padding(start = (48f * scale).dp)
                )
            }
        }
    }
}

/* ---------- Page 2 : Use Remote ---------- */
@Composable
private fun RemotePage(
    scale: Float,
    vm: LoginViewModel,
    ui: LoginUiState,
    navController: NavHostController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    val emailReq  = remember { FocusRequester() }
    val passReq   = remember { FocusRequester() }
    val signInReq = remember { FocusRequester() }   // <- for focusing the Sign In button

    val fieldWidth = (680f * scale).dp
    val signInHeightDp = (72f * scale).dp           // <- increased button height

    val context = LocalContext.current

    LaunchedEffect(ui) {
        when (ui) {
            is LoginUiState.Success -> {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is LoginUiState.Error -> {
                val msg = ui.msg.ifBlank {
                    "User Name or Password incorrect or not found"
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (40f * scale).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .width(fieldWidth)
                .focusRequester(emailReq),
            singleLine = true,
            placeholder = { Text("Email") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onNext = { passReq.requestFocus() }
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = (18f * scale).sp, color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White,
                focusedBorderColor = Color(0xFFB0B0B0), unfocusedBorderColor = Color(0xFF777777),
                focusedContainerColor = Color(0xFF202020), unfocusedContainerColor = Color(0xFF202020),
                focusedPlaceholderColor = Color(0xFF9E9E9E), unfocusedPlaceholderColor = Color(0xFF9E9E9E)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(Modifier.height((16f * scale).dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .width(fieldWidth)
                .focusRequester(passReq),
            singleLine = true,
            placeholder = { Text("Password") },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Text(
                    if (showPass) "🙈" else "👁",
                    color = Color(0xFFDDDDDD),
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clickable { showPass = !showPass }
                )
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                // ✔ When pressing Enter on password field → focus the Sign In button
                onDone = { signInReq.requestFocus() }
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = (18f * scale).sp, color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White, unfocusedTextColor = Color.White, cursorColor = Color.White,
                focusedBorderColor = Color(0xFFB0B0B0), unfocusedBorderColor = Color(0xFF777777),
                focusedContainerColor = Color(0xFF202020), unfocusedContainerColor = Color(0xFF202020),
                focusedPlaceholderColor = Color(0xFF9E9E9E), unfocusedPlaceholderColor = Color(0xFF9E9E9E)
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(Modifier.height((24f * scale).dp))

        // Sign In button (focusable + taller)
        Box(
            modifier = Modifier
                .width(fieldWidth)
                .height(signInHeightDp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFF1A1A))
                .focusRequester(signInReq)       // <- receives focus from password onDone
                .clickable {
                    vm.doLogin(context, email.trim(), password)
                }
            ,
            contentAlignment = Alignment.Center
        ) {
            Text("Sign In", color = Color.White, fontSize = (24f * scale).sp, fontWeight = FontWeight.Bold)
        }
    }
    LaunchedEffect(Unit) { emailReq.requestFocus() }
}


@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color.White else Color.Transparent
    val fg = if (isSelected) Color.Black else Color.White

    Box(
        modifier = modifier
            .padding(horizontal = (2f * scale).dp, vertical = (1f * scale).dp) // tighter spacing inside capsule
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = (22f * scale).dp, vertical = (6f * scale).dp), // ↓ reduced vertical padding
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = fg,
            fontSize = (17f * scale).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun generateQrBitmap(text: String, sizePx: Int): Bitmap {
    val matrix: BitMatrix = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        sizePx,
        sizePx
    )

    val w = matrix.width
    val h = matrix.height
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

    for (y in 0 until h) {
        for (x in 0 until w) {
            bmp.setPixel(x, y, if (matrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bmp
}


@Composable
private fun TvTextField(
    label: String,
    isPassword: Boolean,
    scale: Float,
    focusRequester: FocusRequester,
    nextFocus: FocusRequester?
) {
    var value by remember { mutableStateOf("") }
    val kb = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(6.dp)

    val options = androidx.compose.foundation.text.KeyboardOptions(
        keyboardType = if (isPassword)
            androidx.compose.ui.text.input.KeyboardType.Password
        else
            androidx.compose.ui.text.input.KeyboardType.Email,
        imeAction = if (nextFocus != null)
            androidx.compose.ui.text.input.ImeAction.Next
        else
            androidx.compose.ui.text.input.ImeAction.Done
    )
    val actions = androidx.compose.foundation.text.KeyboardActions(
        onNext = { nextFocus?.requestFocus() },
        onDone = { kb?.hide() }
    )

    // Use Material3 OutlinedTextField directly — no wrapper Box, no fixed height.
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = { value = it },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) kb?.show() },
        textStyle = TextStyle(
            color = Color.White,
            fontSize = (18f * scale).sp
        ),
        placeholder = {
            Text(
                text = label,
                color = Color(0xFFBDBDBD),
                fontSize = (18f * scale).sp
            )
        },
        visualTransformation = if (isPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = options,
        keyboardActions = actions,
        shape = shape,
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedBorderColor = Color(0xFFB0B0B0),
            unfocusedBorderColor = Color(0xFF777777),
            focusedContainerColor = Color(0xFF4A4A4A),
            unfocusedContainerColor = Color(0xFF4A4A4A)
        )
    )
}

