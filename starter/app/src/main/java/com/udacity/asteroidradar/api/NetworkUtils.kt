package com.udacity.asteroidradar.api

import android.net.ParseException
import android.os.Build
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.domain.DailyPicture
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Parse asteroids from Map. [asteroidsFullData] is a strange object created by Moshi from
 * JSON data. It contains many LinkedHashTreeMap object from Moshi. We can't work with this type.
 * So we work with common Map.
 * */
fun parseAsteroids(asteroidsFullData: Map<*, *>): List<Asteroid> {
    Timber.i("parseAsteroids() begin")
    val nearEarthObjects: Map<*, *>
    try {
        nearEarthObjects = getNearEarthObjects(asteroidsFullData)
        if (nearEarthObjects.isEmpty()) return emptyList()
    } catch (exc: ParseException) {
        Timber.e("parseAsteroids() Parse error on parsing asteroids $exc.message")
        return emptyList()
    }

    val domainAsteroids = mutableListOf<Asteroid>()

    for (asteroidsOfDayPair: Pair<*, *> in nearEarthObjects.toList()) {
        val asteroidsOfDayList: ArrayList<*> = asteroidsOfDayPair.second as ArrayList<*>

        for (asteroid in asteroidsOfDayList) {
            if (asteroid is Map<*, *>) {
                val asteroidParseResult =
                    try {
                        parseAsteroid(asteroid)
                    } catch (exc: ParseException) {
                        Timber.e("parseAsteroids() Parse error on parsing single asteroid $exc.message")
                    }

                if (asteroidParseResult is Asteroid) {
                    domainAsteroids += asteroidParseResult
                    Timber.i("Parsed asteroid: ${asteroidParseResult.codename} : ${asteroidParseResult.closeApproachDate}")
                }
            }
        }

    }

    Timber.i("parseAsteroids() end, count of asteroids = ${domainAsteroids.count()}, last date: ${domainAsteroids.last().closeApproachDate}")
    return domainAsteroids
}

private fun parseAsteroid(asteroid: Map<*, *>): Asteroid {
    val id: Long = (asteroid["id"] as String)
        .toLong()
    val codename = asteroid["name"] as String

    val closeApproachData = (asteroid["close_approach_data"]
            as ArrayList<*>)[0] as Map<*, *>

    val closeApproachDate = (closeApproachData["close_approach_date"]) as String

    val absoluteMagnitude = asteroid["absolute_magnitude_h"]
            as Double

    val estimatedDiameterKm = ((asteroid["estimated_diameter"]
            as Map<*, *>)["kilometers"]
            as Map<*, *>)["estimated_diameter_max"]
            as Double

    val relativeVelocity = ((((closeApproachData["relative_velocity"])
            as Map<*, *>)["kilometers_per_second"])
            as String)
        .toDouble()

    val distanceFromEarth = ((((closeApproachData["miss_distance"])
            as Map<*, *>)["astronomical"])
            as String)
        .toDouble()

    val isPotentiallyHazardous = asteroid["is_potentially_hazardous_asteroid"]
            as Boolean

    val date: Date = getDateFromString(closeApproachDate, id)

    return Asteroid(
        id = id,
        codename = codename,
        closeApproachDate = date,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameterKm,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}

fun parseDailyPicture(rawPictureData: Map<*, *>): DailyPicture {
    Timber.i("parseDailyPicture() start")

    val rawDate = rawPictureData["date"] as String
    val date = DateUtils.getDateFromString(
        rawDate,
        "Parse of date of daily picture failed."
    )

    return DailyPicture(
        id = rawDate,
        date = date,
        title = rawPictureData["title"] as String,
        mediaType = rawPictureData["media_type"] as String,
        url = rawPictureData["url"] as String
    )
}

private fun getDateFromString(date: String, id: Long): Date {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.parse(date)
        ?: throw Exception(
            "parse of closeApproachDate $date for asteroid with id $id leads to null"
        )
}

private fun getNearEarthObjects(asteroidsFullData: Map<*, *>) =
    asteroidsFullData["near_earth_objects"] as Map<*, *>