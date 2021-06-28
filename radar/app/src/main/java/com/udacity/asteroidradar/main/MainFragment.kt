package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.network.AsteroidsApiFilter
import timber.log.Timber

/**
 * This fragment shows the list of current asteroids and picture of the day.
 */
class MainFragment : Fragment() {

    /**
     * Lazily initialize our [MainViewModel].
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        ).get(MainViewModel::class.java)
    }

    /**
     * ListAdapter of recycler view. We hold the reference so we can actualize
     * the reference to asteroids data.
     */
    private lateinit var asteroidListAdapter: AsteroidListAdapter

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the MainFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("onCreateView start")

        val binding = FragmentMainBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the MainViewModel
        binding.viewModel = viewModel

        // Create adapter
        // - set it as adapter of the RecyclerView
        // - save it additionally in fragment
        binding.asteroidsRecyclerview.adapter = AsteroidListAdapter(
            AsteroidListAdapter.OnClickListener { asteroid ->
                Timber.i("asteroid clicked: ${asteroid.codename}")
                // start navigation to detail screen
                viewModel.displayAsteroidDetails(asteroid)
            }, context?.applicationContext
        )
            .apply {
                asteroidListAdapter = this
            }

        /**
         * Navigate to detail screen.
         * Observe the navigateToSelected.. LiveData and navigate when it isn't null.
         * After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
         * for another navigation event.
         */
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            Timber.i("observing navigateToSelectedAsteroid")
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.navigateToAsteroidDetailsComplete()
            }
        })

        // Observe daily picture data
        // Load picture by picasso. Bind it to view.
        viewModel.dailyPictureData.observe(viewLifecycleOwner, Observer {
            it?.let { dailyPicture ->
                Timber.i("Observer of dailyPictureData: ${dailyPicture.url}")
                Picasso.get().load(dailyPicture.url).into(binding.activityMainImageOfTheDay)
                binding.activityMainImageOfTheDay.contentDescription = dailyPicture.title
                binding.titleOfDailyPictureTextView.text = dailyPicture.title
            }
        })

        setHasOptionsMenu(true)
        Timber.i("onCreateView end")
        return binding.root
    }

    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_overflow_menu, menu)
    }

    /**
     * Updates the filter in the [MainViewModel] when the menu items are selected from the
     * overflow menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("onOptionsItemSelected(): MenuItem: ${item.itemId}")
        viewModel.filterAsteroids( //call updateFilter on the view model
            when (item.itemId) {
                R.id.show_week_asteroids_menu -> AsteroidsApiFilter.VIEW_WEEK_ASTEROIDS
                R.id.show_saved_asteroids_menu -> AsteroidsApiFilter.VIEW_SAVED_ASTEROIDS
                else -> AsteroidsApiFilter.VIEW_TODAY_ASTEROIDS
            }
        )
        updateAsteroidsReferenceOfAsteroidListAdapter()
        return true // Because we've handled the menu item.
    }

    /**
     * Create observer, which observes asteroids of view model.
     * Submits changes to asteroidListAdapter.
     * Background: Variable asteroids gets not only changed/new data but also new LiveData object
     * after filter call.
     */
    private fun updateAsteroidsReferenceOfAsteroidListAdapter() {
        viewModel.asteroids.observe(viewLifecycleOwner) {
            asteroidListAdapter.submitList(it)
            Timber.i("asteroidListAdapter was called with submitList")
            Timber.i("viewModel.asteroids contains ${viewModel.asteroids.value?.count()} asteroids")

        }
    }
}
