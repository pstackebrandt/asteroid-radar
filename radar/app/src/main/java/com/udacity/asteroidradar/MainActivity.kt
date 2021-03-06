package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * This is a single activity application that uses the Navigation library. Content is displayed
 * by Fragments.
 */
class MainActivity : AppCompatActivity() {

    /**
     * MainActivity is only responsible for setting the content view that contains the
     * Navigation Host.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
