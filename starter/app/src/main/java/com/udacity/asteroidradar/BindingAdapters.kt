package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.main.AsteroidListAdapter

/**
 * Bind corresponding hazard smiley icon to [imageView].
 * Decision depends on [isHazardous].
 */
@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

/**
 * Bind corresponding asteroid hazardousness image to [imageView].
 * Decision depends on [isHazardous]
 */
@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

/** Get formatted string with unit au (Astronomical unit) */
@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

/** Get formatted string with unit km */
@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

/** Get formatted string with unit km/s */
@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}


/** Seems to add data to adapter of recycler view. */
@BindingAdapter("asteroidList")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    asteroids: List<Asteroid>?
) {
    val adapter = recyclerView.adapter as AsteroidListAdapter
    adapter.submitList(asteroids) {
        // scroll the list to the top after the diffs are calculated and posted
         recyclerView.scrollToPosition(0)
    }
}