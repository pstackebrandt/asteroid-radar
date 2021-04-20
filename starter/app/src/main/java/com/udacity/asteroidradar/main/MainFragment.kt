package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import timber.log.Timber

/**
 * This fragment shows the list of current asteroids and picture of the day.
 */
class MainFragment : Fragment() {

    /**
     * Lazily initialize our [MainViewModel].
     */
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

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

        // Sets the adapter of the RecyclerView
        binding.asteroidsRecyclerview.adapter = AsteroidListAdapter(
            AsteroidListAdapter.OnClickListener { asteroid ->
                // todo start navigation to detail screen
                Timber.i("asteroid clicked: ${asteroid.codename}")
                viewModel.displayAsteroidDetails(asteroid)
            })

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

        setHasOptionsMenu(true)
        Timber.i("onCreateView end")
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return true
    }
}
