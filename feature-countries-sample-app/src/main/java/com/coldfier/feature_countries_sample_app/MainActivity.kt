package com.coldfier.feature_countries_sample_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_countries.ui.countries_list.CountriesListFragment

class MainActivity : AppCompatActivity(), HasDependencies {

    override val depsMap: DepsMap = mapOf(CountriesDeps::class.java to object : CountriesDeps {})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<FragmentContainerView>(R.id.container)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, CountriesListFragment(), "TAG")
            .commit()

    }
}