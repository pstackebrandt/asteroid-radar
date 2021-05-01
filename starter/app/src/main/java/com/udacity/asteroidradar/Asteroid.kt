package com.udacity.asteroidradar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


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
                    val closeApproachDate: Date,
                    val absoluteMagnitude: Double,
                    val estimatedDiameter: Double,
                    val relativeVelocity: Double,
                    val distanceFromEarth: Double,
                    val isPotentiallyHazardous: Boolean) : Parcelable