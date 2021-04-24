package com.udacity.asteroidradar

import android.os.Parcelable
import androidx.lifecycle.LiveData
import kotlinx.android.parcel.Parcelize

/**
 * This data class defines an asteroid which includes the codename and it's hazardousness.
 *
 * The property names of this data class are used by Moshi to match the names of values in JSON.
 *
 * Gets asteroid information from the asteroids API Retrofit service and updates the
 * asteroid list. The Retrofit service returns a coroutine
 * Deferred, which we await to get the result of the transaction.
 */
@Parcelize
data class Asteroid(val id: Long,
                    val codename: String,
                    val closeApproachDate: String,
                    val absoluteMagnitude: Double = 0.0,
                    val estimatedDiameter: Double = 0.0,
                    val relativeVelocity: Double = 0.0,
                    val distanceFromEarth: Double = 0.0,
                    val isPotentiallyHazardous: Boolean) : Parcelable