package com.projects.a122mmtv.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ViewMovieDetail(
    modifier: Modifier = Modifier,
    navController: NavController
){
    Text(
        "Movie Detail",
        fontSize = 16.sp
    )
}