package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

/**
 * The [ViewModel] that is attached to the [MainFragment].
 */
class MainViewModel : ViewModel() {

    // Internally, we use a MutableLiveData, because we will be updating the List of Asteroids
    // with new values
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

//    init {
//        val asteroids = mutableListOf<Asteroid>()
//        _asteroids.value = asteroids
//    }

    init {
        addExampleData()
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
    }
}