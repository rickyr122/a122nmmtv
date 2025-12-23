package com.projects.a122mmtv.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController){
    Text(
        "Profile Page",
        fontSize = 16.sp
    )
}