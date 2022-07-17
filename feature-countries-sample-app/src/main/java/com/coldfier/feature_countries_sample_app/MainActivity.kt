package com.coldfier.feature_countries_sample_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.feature_countries.CountriesDeps
import com.coldfier.feature_country_detail.CountryDetailDeps

class MainActivity : AppCompatActivity(), HasDependencies {

    private val countriesDeps = object : CountriesDeps {
        override fun navigateToCountryDetailFragment(country: Country) {
            countryDetailDeps.localCountry = country
            findNavController(R.id.container)
                .navigate(R.id.action_countriesListFragment_to_countryDetailFragment)
        }
    }

    private val countryDetailDeps = object : CountryDetailDeps {
        var localCountry = Country()

        override fun getCountry(): Country {
            return localCountry
        }
    }

    override val depsMap: DepsMap = mapOf(
        CountriesDeps::class.java to countriesDeps,
        CountryDetailDeps::class.java to countryDetailDeps
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

