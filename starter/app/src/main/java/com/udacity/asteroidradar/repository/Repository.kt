package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.api.parseAsteroids
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /** The internal MutableLiveData string that stores the status of
    the most recent request.*/
//    private val _status = MutableLiveData<AsteroidsApiStatus>()

    val date: Date = DateUtils.getDateFromString("2021-04-17")

    /**
     * Get list of asteroids.
     * We return domain objects, which are agnostic of Network or Database.
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids(date)) {
            it.asDomainModel()
//            Transformations.map(database.asteroidDao.getAsteroids()) {
//            it.asDomainModel()
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
        withContext(Dispatchers.IO) {
            val asteroidsFullData = AsteroidService.getAsteroids()
                    as Map<*, *>
            val asteroids: List<Asteroid> = parseAsteroids(asteroidsFullData)

            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }
    }
}
