package com.projects.a122mmtv.utility

import android.content.Context
import androidx.compose.ui.graphics.Color
import org.json.JSONObject

object BannerStorage {
    private const val PREF_NAME = "banner_cache"
    private const val CACHE_DURATION = 60 * 60 * 1000L // 1 hour in ms

    private fun keyData(type: String, userId: Int) =
        "banner_data_${type}_u$userId"

    private fun keyTime(type: String, userId: Int) =
        "banner_time_${type}_u$userId"

    fun saveBanner(
        context: Context,
        type: String,
        userId: Int,
        bannerJson: String
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(keyData(type, userId), bannerJson)
            .putLong(keyTime(type, userId), System.currentTimeMillis())
            .apply()
    }

    fun loadBanner(
        context: Context,
        type: String,
        userId: Int
    ): Pair<JSONObject?, Boolean> {

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val time = prefs.getLong(keyTime(type, userId), 0L)
        val expired = (System.currentTimeMillis() - time) > CACHE_DURATION

        val jsonString = prefs.getString(keyData(type, userId), null)
        val bannerJson = jsonString?.let { JSONObject(it) }

        return Pair(bannerJson, expired)
    }
}