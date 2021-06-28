package com.udacity.asteroidradar

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.DateUtils.Companion.toYearMonthsDays
import com.udacity.asteroidradar.main.AsteroidListAdapter
import java.util.*

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
        imageView.contentDescription =
            imageView.resources.getString(R.string.asteroidStatusImage_asteroidHazardous_contentDescription)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription =
            imageView.resources.getString(R.string.asteroidStatusImage_asteroidSafe_contentDescription)
    }
}

/** Get formatted string with unit au (Astronomical unit) */
@BindingAdapter("dateWithoutTime")
fun bindTextViewToDate(textView: TextView, date: Date) {
    textView.text = date.toYearMonthsDays()
}

/** Description for TalkBack. */
@BindingAdapter("closeApproachDateDescription")
fun bindDescriptionToCloseApproachDateDescription(linearLayout: LinearLayout, asteroid: Asteroid) {
    val context = linearLayout.context
    linearLayout.contentDescription =
        "${context.getString(R.string.close_approach_data_title)} is ${asteroid.closeApproachDate.toYearMonthsDays()}"
}

/** Description for TalkBack. */
@BindingAdapter("absoluteMagnitudeDescription")
fun bindDescriptionToAbsoluteMagnitudeDescription(viewGroup: ViewGroup, asteroid: Asteroid) {
    val context = viewGroup.context
    viewGroup.contentDescription = "${context.getString(R.string.absolute_magnitude_title)} " +
            "is ${
                formatNumberWithUnabbreviatedAstronomicalUnit(
                    context,
                    asteroid.absoluteMagnitude
                )
            }."
}

/** Description for TalkBack. */
@BindingAdapter("estimatedDiameterDescription")
fun bindDescriptionToEstimatedDiameterDescription(viewGroup: ViewGroup, asteroid: Asteroid) {
    val context = viewGroup.context
    viewGroup.contentDescription = "${context.getString(R.string.estimated_diameter_title)} " +
            "is ${getFormattedValueWithKm(context, asteroid.estimatedDiameter)} "
}

/** Get formatted string with unit au (Astronomical unit) */
@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = formatNumberWithAstronomicalUnit(context, number)
}

/** Get formatted string with unit km */
@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = getFormattedValueWithKm(context, number)
}

/** Get formatted string with unit km/s */
@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = getFormattedValueWithKmPerSecond(context, number)
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

private fun formatNumberWithAstronomicalUnit(
    context: Context,
    number: Double
) = String.format(context.getString(R.string.astronomical_unit_format), number)

private fun formatNumberWithUnabbreviatedAstronomicalUnit(
    context: Context,
    number: Double
) = String.format(context.getString(R.string.astronomical_unit_format_unabbreviated_plural), number)

private fun getFormattedValueWithKmPerSecond(
    context: Context,
    number: Double
) = String.format(context.getString(R.string.km_s_unit_format), number)

private fun getFormattedValueWithKm(
    context: Context,
    number: Double
) = String.format(context.getString(R.string.km_unit_format), number)
