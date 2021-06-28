package com.udacity.asteroidradar.network

import com.squareup.moshi.Json
import java.util.*

data class NetworkDailyPicture(
    @Json(name = "media_type")
    val id: String,
    val date: Date,
    val mediaType: String,
    val title: String,
    val url: String
)