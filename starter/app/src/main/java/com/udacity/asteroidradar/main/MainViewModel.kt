package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.AsteroidsApiFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

enum class AsteroidsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel(application: Application) : ViewModel() {

    // Internally, we use a MutableLiveData, because we will be updating the List of Asteroids
    // with new values
    // private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    // val asteroids: LiveData<List<Asteroid>>
    // get() = _asteroids

    // Internally, we use a MutableLiveData to handle navigation to the selected asteroid
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation asteroid
    /** If navigation is required, this property contains the [Asteroid] to which we want to navigate.
     * Tells thereby whether navigation is required. */
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init {
        refreshAsteroids()
    }

    val asteroids: LiveData<List<Asteroid>> = asteroidsRepository.asteroids

    fun filterAsteroids(filter: AsteroidsApiFilter) {
        Timber.i("updateFilter(): VIEW_TODAY_ASTEROIDS: $filter")
        when (filter) {
            AsteroidsApiFilter.VIEW_TODAY_ASTEROIDS -> {
                val date = DateUtils.getDateWithoutTime()
                asteroidsRepository.filterAsteroids(date, date)
            }
            AsteroidsApiFilter.VIEW_WEEK_ASTEROIDS -> {
                val startDate = DateUtils.getDateWithoutTime()
                asteroidsRepository.filterAsteroids(
                    startDate,
                    DateUtils.getDate6DaysLater(startDate)
                )
            }
            else -> {   // show all saved asteroids
                asteroidsRepository.filterAsteroids()
            }
        }
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                Timber.i("refreshAsteroids(): before service call ")
                asteroidsRepository.refreshAsteroids()
                Timber.i("refreshAsteroids(): after service call ")
            } catch (e: Exception) {
                Timber.i("refreshAsteroids(): exception ${e.message}")
                Timber.i("refreshAsteroids(): exception ${e.stackTrace}")
            }
        }
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


    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}