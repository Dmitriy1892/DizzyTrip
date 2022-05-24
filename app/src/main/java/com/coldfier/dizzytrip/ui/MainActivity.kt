package com.coldfier.dizzytrip.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.coldfier.core_utils.di.ViewModelFactory
import com.coldfier.dizzytrip.DizzyTripApplication
import com.coldfier.dizzytrip.R
import com.coldfier.dizzytrip.databinding.ActivityMainBinding
import com.coldfier.dizzytrip.di.subcomponent.MainActivitySubcomponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

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
            .navController(navController)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityComponent.inject(this)
    }
}