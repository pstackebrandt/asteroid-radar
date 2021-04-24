package com.udacity.asteroidradar.api

import android.net.ParseException
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun parseAsteroidsJsonResult(jsonResult: JSONObject):
        ArrayList<Asteroid> {
    val nearEarthObjectsJson = jsonResult.getJSONObject("near_earth_objects")

    val asteroidList = ArrayList<Asteroid>()

    val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()
    for (formattedDate in nextSevenDaysFormattedDates) {
        val dateAsteroidJsonArray = nearEarthObjectsJson.getJSONArray(formattedDate)

        for (i in 0 until dateAsteroidJsonArray.length()) {
            val asteroidJson = dateAsteroidJsonArray.getJSONObject(i)
            val id = asteroidJson.getLong("id")
            val codename = asteroidJson.getString("name")
            val absoluteMagnitude = asteroidJson.getDouble("absolute_magnitude_h")
            val estimatedDiameter = asteroidJson.getJSONObject("estimated_diameter")
                .getJSONObject("kilometers").getDouble("estimated_diameter_max")

            val closeApproachData = asteroidJson
                .getJSONArray("close_approach_data").getJSONObject(0)
            val relativeVelocity = closeApproachData.getJSONObject("relative_velocity")
                .getDouble("kilometers_per_second")
            val distanceFromEarth = closeApproachData.getJSONObject("miss_distance")
                .getDouble("astronomical")
            val isPotentiallyHazardous = asteroidJson
                .getBoolean("is_potentially_hazardous_asteroid")

            val asteroid = Asteroid(
                id, codename, formattedDate, absoluteMagnitude,
                estimatedDiameter, relativeVelocity, distanceFromEarth, isPotentiallyHazardous
            )
            asteroidList.add(asteroid)
        }
    }

    return asteroidList
}

private fun getNextSevenDaysFormattedDates(): ArrayList<String> {
    val formattedDateList = ArrayList<String>()

    val calendar = Calendar.getInstance()
    for (i in 0..Constants.DEFAULT_END_DATE_DAYS) {
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        formattedDateList.add(dateFormat.format(currentTime))
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return formattedDateList
}

/**
 * Parse asteroids from Map. [asteroidsFullData] is a strange object created by Moshi from
 * JSON data. It contains many LinkedHashTreeMap object from Moshi. We can't work with this type.
 * So we work with common Map.
 * */
fun parseAsteroids(asteroidsFullData: Map<*, *>): List<Asteroid> {

    val nearEarthObjects: Map<*, *>
    try {
        nearEarthObjects = getNearEarthObjects(asteroidsFullData)
        if (nearEarthObjects.isEmpty()) return emptyList()
    } catch (exc: ParseException) {
        Timber.e("Parse error on parsing asteroids $exc.message")
        return emptyList()
    }

    val asteroidsOfFirstDay = nearEarthObjects.entries.toTypedArray()[0].value
    // todo use data of all required days

    val domainAsteroids = mutableListOf<Asteroid>()

    if (asteroidsOfFirstDay is ArrayList<*>) {
        for (asteroid in asteroidsOfFirstDay) {
            if (asteroid is Map<*, *>) {

                val asteroidParseResult =
                    try {
                        parseAsteroid(asteroid)
                    } catch (exc: ParseException) {
                        Timber.e("Parse error on parsing single asteroid $exc.message")
                    }

                if (asteroidParseResult is Asteroid) {
                    domainAsteroids += asteroidParseResult
                }
            }
        }
    }

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

    return Asteroid(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachDate,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameterKm,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}

private fun getNearEarthObjects(asteroidsFullData: Map<*, *>): Map<*, *> {
    return asteroidsFullData["near_earth_objects"] as Map<*, *>
}