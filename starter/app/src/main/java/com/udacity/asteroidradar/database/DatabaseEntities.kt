package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.domain.DailyPicture
import java.util.*


/** Instantiate database asteroid (database table) */
@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: Date,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

/** Transform database asteroids into domain asteroids. */
@JvmName("databaseAsteroidsAsDomainModel")
fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            it.id,
            it.codename,
            it.closeApproachDate,
            it.absoluteMagnitude,
            it.estimatedDiameter,
            it.relativeVelocity,
            it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }
}

/** Instantiate database daily pictures (database table) */
@Entity
data class DatabaseDailyPicture constructor(
    @PrimaryKey
    val id: String,
    val date: Date,
    val mediaType: String,
    val title: String,
    val url: String
)

/** Transform database daily pictures into domain daily . */
@JvmName("databaseDailyPicturesAsDomainModel")
fun List<DatabaseDailyPicture>.asDomainModel(): List<DailyPicture> {
    return map {
        DailyPicture(
            id = it.id,
            date = it.date,
            mediaType = it.mediaType,
            title = it.title,
            url = it.url
        )
    }
}
