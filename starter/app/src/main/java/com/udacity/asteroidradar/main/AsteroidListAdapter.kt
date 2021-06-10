package com.udacity.asteroidradar.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.DateUtils.Companion.toYearMonthsDays
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidListItemBinding
import timber.log.Timber

/**
 * Adapter for AsteroidList RecyclerView
 * Adapter with ViewHolder and Diff
 */
class AsteroidListAdapter(val onClickListener: OnClickListener, val context: Context?) :
    ListAdapter<Asteroid, AsteroidListAdapter.AsteroidListViewHolder>(DiffCallback) {

    /** Contains functionality to check whether chapters are same or have same content.  */
    companion object DiffCallback :
        DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Specific ViewHolder for the RecyclerView.
     */
    class AsteroidListViewHolder(private var binding: AsteroidListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Bind ViewHolder to [Asteroid] and [OnClickListener]. */
        fun bind(listener: OnClickListener, asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.clickListener = listener

            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements.
            binding.executePendingBindings()
        }

        companion object {
            /** Get [AsteroidListViewHolder] */
            fun from(parent: ViewGroup): AsteroidListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidListItemBinding.inflate(layoutInflater, parent, false)

                return AsteroidListViewHolder(binding)
            }
        }
    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs a new ViewHolder.
     *
     * A ViewHolder holds a view for the [RecyclerView] as well as providing additional information
     * to the RecyclerView such as where on the screen it was last drawn during scrolling.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):
            AsteroidListViewHolder {
        Timber.i("adapter: onCreateViewHolder")
        return AsteroidListViewHolder.from(parent)
    }

    /**
     * Part of the RecyclerView adapter, called when RecyclerView needs to show an item.
     *
     * The ViewHolder passed may be recycled, so make sure that this sets any properties that
     * may have been set previously.
     *
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the ViewHolder.itemView to reflect the item at the given
     * position.
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use ViewHolder.getBindingAdapterPosition which
     * will have the updated adapter position.
     * (Replaces the contents of a view (invoked by the layout manager))
     */
    override fun onBindViewHolder(
        holder: AsteroidListViewHolder,
        position: Int
    ) {
        Timber.i("onBindViewHolder")
        val asteroid = getItem(position)
        holder.bind(onClickListener, asteroid)
        holder.itemView.contentDescription =
            "${getTextualHasardousnessRating(asteroid.isPotentiallyHazardous)} " +
                    "Asteroid with codename ${asteroid.codename}. " +
                    "Close approach date is ${asteroid.closeApproachDate.toYearMonthsDays()}."
    }

    private fun getTextualHasardousnessRating(isPotentiallyHazardous: Boolean) =
        if (isPotentiallyHazardous) {
            context?.getString(R.string.HazardousText) ?: "hazardous"
        } else {
            context?.getString(R.string.SafeText) ?: "safe"
        }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Asteroid]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Asteroid]
     */
    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}


