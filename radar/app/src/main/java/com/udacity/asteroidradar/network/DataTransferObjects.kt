package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.DatabaseDailyPicture
import com.udacity.asteroidradar.domain.DailyPicture

/**
 * File contains DataTransferObjects.
 * These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 * Converts from domain objects to database objects
 * Antetype of this method was DevBytes NetworkVideoContainer.asDomainModel().
 * There it's more consistent because they convert from Network object to database object.
 * Currently we don't have a useful network object because we parse directly into domain object.
 */
fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return this.map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )

    }.toTypedArray()
}

/**
 * Converts from domain objects to database objects
 * Antetype of this method was DevBytes NetworkVideoContainer.asDomainModel().
 */
fun List<DailyPicture>.asDatabaseModel(): Array<DatabaseDailyPicture> {
    return this.map {
        DatabaseDailyPicture(
            id = it.id,
            date = it.date,
            mediaType = it.mediaType,
            title = it.title,
            url = it.url
        )
    }.toTypedArray()
}

/**
 * Converts from domain object to database objects
 * Antetype of this method was DevBytes NetworkVideoContainer.asDomainModel().
 */
fun DailyPicture.asDatabaseModel(): DatabaseDailyPicture {
    return DatabaseDailyPicture(
        id = this.id,
        date = this.date,
        mediaType = this.mediaType,
        title = this.title,
        url = this.url
    )
}