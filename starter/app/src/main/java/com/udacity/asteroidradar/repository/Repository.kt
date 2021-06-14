package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.DateUtils.Companion.toAsteroidsDateString
import com.udacity.asteroidradar.api.parseAsteroids
import com.udacity.asteroidradar.api.parseDailyPicture
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.domain.DailyPicture
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import com.udacity.asteroidradar.network.AsteroidApiService.AsteroidsApi.retrofitService as AsteroidService

/**
 * Repository for fetching Asteroids data from the network and storing it on disk.
 *
 * Repository modules handle data operations. They provide a clean API so that the rest of
 * the app can retrieve this data easily. They know where to get the data from and what API
 * when data is updated. You can consider repositories to be mediators between different data
 * sources, in our case it mediates between a network API and an offline database cache.
 */
class AsteroidsRepository(private val database: AsteroidsDatabase) {

    companion object {
        const val ASTEROIDS_DATE_FORMAT = "yyyy-MM-dd"
    }

    val date: Date = DateUtils.getDateWithoutTime()


    /**
     * Refresh the Asteroid data stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the I0 dispatcher. By switching to the I0 dispatcher using 'withContext this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe asteroids.
     */
    suspend fun refreshAsteroids() {
        val currentDate = DateUtils.getDateWithoutTime()
        val endDate = DateUtils.getDate6DaysLater(currentDate).toAsteroidsDateString()
        val startDate: String = currentDate.toAsteroidsDateString()
        Timber.i("refreshAsteroids() before server call. startDate: $startDate, endDate: $endDate")

        withContext(Dispatchers.IO) {
            val asteroidsFullData = AsteroidService.getAsteroids(startDate, endDate)
                    as Map<*, *>
            val asteroids: List<Asteroid> = parseAsteroids(asteroidsFullData)

            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }

        Timber.i("refreshAsteroids() after server call. startDate: $startDate, endDate: $endDate")
    }

    suspend fun refreshDailyPicture() {
        Timber.i("refreshDailyPicture() before server call.")

        withContext(Dispatchers.IO) {
            try {
                val rawDailyPicture = AsteroidService.getDailyPictureData() as Map<*, *>
                val dailyPicture: DailyPicture = parseDailyPicture(rawDailyPicture)
                Timber.i("refreshDailyPicture() rawPictureDataAny: $rawDailyPicture")
                database.dailyPictureDao.insert(dailyPicture.asDatabaseModel())
            } catch (exc: Exception) {
                Timber.e("refreshDailyPicture()  ${exc.message}")
            }
        }

        Timber.i("refreshDailyPicture() after server call.")
    }

    /**
     * Delete all asteroids with date before [endDate]. Delete considers date and time given.
     * todo add tests
     */
    suspend fun deleteAsteroidsBefore(endDate: Date) {
        Timber.i("deleteAsteroidsBefore() before database call. endDate: $endDate")

        withContext(Dispatchers.IO) {
            val deletedElementsCount: Int = database.asteroidDao.deleteAllBefore(endDate)
            Timber.i("deleteAsteroidsBefore() after server call. deleted elements: $deletedElementsCount")
        }
        Timber.i("deleteAsteroidsBefore() after server call.")
    }
}

