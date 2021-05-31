package com.udacity.asteroidradar.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * This data class defines a NASA daily picture.
 */
@Parcelize
data class DailyPicture(
    val id: String,
    val date: Date,
    val mediaType: String,
    val title: String,
    val url: String
) : Parcelable
