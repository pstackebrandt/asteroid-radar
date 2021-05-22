package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.DateUtils
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.AsteroidsApiFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * The [ViewModel] that's attached to the [MainFragment].
 */
class MainViewModel(application: Application) : ViewModel() {

    /** Internally, we use a MutableLiveData to handle navigation to the selected asteroid */
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    /** The external immutable LiveData for the navigation asteroid.
     * If navigation is required, this property contains the [Asteroid] to which we want to navigate.
     * Tells thereby whether navigation is required. */
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    /**
     * Asteroids data.
     *
     * We are using Transformations.map() to set the asteroids data.
     * We change the data set if user decides for a new filter.
     * Transformations.map() returns a new LiveData whenever called.
     * This new LiveData is not observed by DataBinding.
     * That's why the display won't get an update.
     * To fix this, we must add observer in fragment after applying the filter and
     * submit the list which will then show the new list.
     *
     * Every time a filter is applied, new LiveData is observed and must be
     * submitted to the list which will compute the diff and update new items.
     */
    var asteroids: LiveData<List<Asteroid>> =
        Transformations.map(                    // todo call filterAsteroids() instead?
            database.asteroidDao.getAsteroidsWithinTimeSpan(
                DateUtils.getDateWithoutTime(),
                DateUtils.getDateOfNextDay(DateUtils.getDateWithoutTime())
            )
        ) {
            it.asDomainModel()
        }

    init {
        refreshAsteroids()
    }

    /** Filters asteroids using an [AsteroidsApiFilter]. */
    fun filterAsteroids(filter: AsteroidsApiFilter) {
        Timber.i("filterAsteroids(): filter: $filter")
        when (filter) {
            AsteroidsApiFilter.VIEW_TODAY_ASTEROIDS -> {
                val date = DateUtils.getDateWithoutTime()
                filterAsteroids(date, date)
            }
            AsteroidsApiFilter.VIEW_WEEK_ASTEROIDS -> {
                val startDate = DateUtils.getDateWithoutTime()
                filterAsteroids(
                    startDate,
                    DateUtils.getDate6DaysLater(startDate)
                )
            }
            else -> {   // show all saved asteroids
                filterAsteroids()
            }
        }
    }

    /**
     * Get list of asteroids. Set time span.
     * We return domain objects, which are agnostic of Network or Database.
     */
    private fun filterAsteroids(startDate: Date? = null, endDate: Date? = null) {
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
            val databaseAsteroidsLiveData: LiveData<List<DatabaseAsteroid>> =
                database.asteroidDao.getAllAsteroids()

            Timber.i("filterAsteroids(): call database.asteroidDao.getAsteroids()")
            Transformations.map(databaseAsteroidsLiveData) {
                it.asDomainModel()
            }
        }

        // Timber.i("filterAsteroids() at end, var asteroid contains ${asteroids.value?.count()} asteroids")
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