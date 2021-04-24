package com.udacity.asteroidradar.main

import com.udacity.asteroidradar.network.AsteroidsApiFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroids
import com.udacity.asteroidradar.network.AsteroidApiService
import kotlinx.coroutines.launch
import timber.log.Timber

enum class AsteroidsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel : ViewModel() {

    /** The internal MutableLiveData string that stores the status of
    the most recent request.*/
    private val _status = MutableLiveData<AsteroidsApiStatus>()

    /* The external immutable LiveData for the status string */
    val status: LiveData<AsteroidsApiStatus>
        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of Asteroids
    // with new values
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    // Internally, we use a MutableLiveData to handle navigation to the selected asteroid
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation asteroid
    /** If navigation is required, this property contains the [Asteroid] to which we want to navigate.
     * Tells thereby whether navigation is required. */
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid


    init {
        //addExampleData()
        getAsteroids()
        // getAsteroids(AsteroidsApiFilter.VIEW_TODAY_ASTEROIDS)
    }

    /**
     * Gets filtered asteroids information from the Asteroids API Retrofit service and
     * updates the [Asteroid] [List] and [AsteroidsApiStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [AsteroidsApiFilter] that is sent as part of the web server request
     */
//    private fun getAsteroids(filter: AsteroidsApiFilter) {
    private fun getAsteroids() {
        viewModelScope.launch {
            _status.value = AsteroidsApiStatus.LOADING

            try {
                Timber.i("getAsteroids(): before service call ")
                val asteroidsFullData = AsteroidApiService.AsteroidsApi.retrofitService.getAsteroids()
                        as Map<*,*>
                val asteroids = parseAsteroids(asteroidsFullData)
                Timber.i("getAsteroids(): asteroids parsed")
                _asteroids.value = asteroids

                _status.value = AsteroidsApiStatus.DONE
            } catch (e: Exception) {

                Timber.i("getAsteroids(): exception ${e.message}")
                Timber.i("getAsteroids(): exception ${e.stackTrace}")
                _status.value = AsteroidsApiStatus.ERROR
                _asteroids.value = ArrayList()
            }
        }
    }

    private fun addExampleData() {
        val asteroids = mutableListOf<Asteroid>()

        asteroids.add(
            Asteroid(
                2465633,
                "465633 (2009 JR5)",
                "2015-09-08\n",
                20.36,
                0.5035469604,
                0.3027478814,
                0.3027478814,
                true
            )
        )

        asteroids.add(
            Asteroid(
                3426410,
                "(2008 QV11)",
                "2015-09-08\n",
                21.0,
                0.3750075218,
                19.7498082027,
                0.2591243925,
                false
            )
        )

        asteroids.add(
            Asteroid(
                3553060,
                "(2010 XT10)",
                "2015-09-08\n",
                26.5,
                0.0297879063,
                19.1530348886,
                0.4917435147,
                false
            )
        )

        _asteroids.value = asteroids

        Timber.i("Dummy asteroids added.")
    }

    /**
     * When the asteroid entry is clicked, set the [_navigateToSelectedAsteroid] [MutableLiveData].
     * @param asteroid The [Asteroid] that was clicked on.
     */
    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure [navigateToSelectedAsteroid] is set to null
     */
    fun navigateToAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}