package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.DateUtils.Companion.getDateWithoutTime
import com.udacity.asteroidradar.DateUtils.Companion.toString
import com.udacity.asteroidradar.api.parseAsteroids
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
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

    /** The internal MutableLiveData string that stores the status of
    the most recent request.*/
    // private val _status = MutableLiveData<AsteroidsApiStatus>()

    val date: Date = DateUtils.getDateWithoutTime()


//    private var _asteroids: LiveData<List<Asteroid>> = MutableLiveData<List<Asteroid>>()
//    val asteroids: LiveData<List<Asteroid>>
//        get() = _asteroids

    /**
     * for temporary test only
     */
    var asteroids2: LiveData<List<Asteroid>> =
        // temp get data for 2 days only
        Transformations.map(database.asteroidDao.getAsteroidsWithinTimeSpan(
            getDateWithoutTime(),
            DateUtils.getDateOfNextDay(getDateWithoutTime()))) {
            it.asDomainModel()
        }

    /**
     * Get list of asteroids initially.
     * We return domain objects, which are agnostic of Network or Database.
     */
    var asteroids: LiveData<List<Asteroid>> =
//        // temp get data for 2 days only
        Transformations.map(database.asteroidDao.getAsteroidsWithinTimeSpan(
            getDateWithoutTime(),
            DateUtils.getDateOfNextDay(getDateWithoutTime()))) {
            it.asDomainModel()
        }
//        Transformations.map(database.asteroidDao.getAllAsteroids()) {
//            it.asDomainModel()
//        }


    /**
     * Get list of asteroids. Set time span.
     * We return domain objects, which are agnostic of Network or Database.
     */
    fun filterAsteroids(startDate: Date? = null, endDate: Date? = null) {
        asteroids = if (startDate != null && endDate != null) {
           // Timber.i("filterAsteroids(): call database.asteroidDao.getAsteroids(startDate = $startDate, endDate = $endDate)")
            Transformations.map(
                database.asteroidDao.getAsteroidsWithinTimeSpan(
                    startDate,
                    endDate
                )
            ) {
                it.asDomainModel()
            }
        } else {
            Timber.i("filterAsteroids(): call database.asteroidDao.getAsteroids()")
            Transformations.map(database.asteroidDao.getAllAsteroids()) {
                it.asDomainModel()
            }
        }

       // Timber.i("filterAsteroids() at end, var asteroid contains ${asteroids.value?.count()} asteroids")
    }

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
        val endDate: String =
            DateUtils.getDate6DaysLater(currentDate).toString(ASTEROIDS_DATE_FORMAT)
        val startDate: String = currentDate.toString(ASTEROIDS_DATE_FORMAT)
        Timber.i("refreshAsteroids() before server call. startDate: $startDate, endDate: $endDate")

        withContext(Dispatchers.IO) {
            val asteroidsFullData = AsteroidService.getAsteroids(startDate, endDate)
                    as Map<*, *>
            val asteroids: List<Asteroid> = parseAsteroids(asteroidsFullData)

            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }

        Timber.i("refreshAsteroids() after server call. startDate: $startDate, endDate: $endDate")
    }
}
