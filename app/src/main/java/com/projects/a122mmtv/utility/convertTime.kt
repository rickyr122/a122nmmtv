package com.projects.a122mmtv.utility

fun formatDurationFromMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return buildString {
        if (hours > 0) append("${hours}h")
        if (remainingMinutes > 0) append(" ${remainingMinutes}m")
    }.trim()
}