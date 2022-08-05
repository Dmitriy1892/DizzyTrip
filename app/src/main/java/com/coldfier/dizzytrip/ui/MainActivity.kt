package com.coldfier.dizzytrip.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.dizzytrip.DizzyTripApplication
import com.coldfier.dizzytrip.R
import com.coldfier.dizzytrip.databinding.ActivityMainBinding
import com.coldfier.dizzytrip.di.subcomponent.MainActivitySubcomponent
import com.coldfier.feature_bookmarks.BookmarksDeps
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_country_detail.CountryDetailDeps
import com.coldfier.feature_map.MapDeps
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasDependencies {

    private val countryDetailDeps = object : CountryDetailDeps{
        override var country: Country = Country()
    }

    private val countriesDeps = object : CountriesDeps {
        override fun navigateToCountryDetailFragment(country: Country) {
            countryDetailDeps.country = country
            navController.navigate(R.id.action_countriesListFragment_to_countryDetailFragment)
        }
    }

    private val mapDeps = object : MapDeps {}

    private val bookmarksDeps = object : BookmarksDeps {
        override fun navigateToDetailScreen(country: Country) {
            countryDetailDeps.country = country
            navController.navigate(R.id.action_bookmarksFragment_to_countryDetailFragment2)
        }
    }

    override val depsMap: DepsMap by lazy { mapOf(
        CountriesDeps::class.java to countriesDeps,

        CountryDetailDeps::class.java to countryDetailDeps,

        MapDeps::class.java to mapDeps,

        BookmarksDeps::class.java to bookmarksDeps) }

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val navController: NavController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navHostFragment.navController
    }

    private val activityComponent: MainActivitySubcomponent by lazy {
        (applicationContext as DizzyTripApplication).appComponent
            .mainActivityComponent()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView = binding.bottomNavigation
        bottomNavView.setupWithNavController(navController)
        bottomNavView.itemIconTintList = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}