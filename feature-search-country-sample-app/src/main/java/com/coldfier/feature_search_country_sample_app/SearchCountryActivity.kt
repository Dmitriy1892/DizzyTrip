package com.coldfier.feature_search_country_sample_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.feature_country_detail.CountryDetailDeps
import com.coldfier.feature_search_country.SearchCountryDeps

class SearchCountryActivity : AppCompatActivity(), HasDependencies {

    private val searchCountryDeps = object : SearchCountryDeps {
        override fun foundCountryClicked(country: Country) {
            countryDetailDeps.country = country
            findNavController(R.id.container)
                .navigate(R.id.action_searchCountryFragment_to_countryDetailFragment)
        }
    }

    private val countryDetailDeps = object : CountryDetailDeps {
        override var country: Country = Country()
    }

    override val depsMap: DepsMap = mapOf(
        SearchCountryDeps::class.java to searchCountryDeps,
        CountryDetailDeps::class.java to countryDetailDeps
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_country)
    }
}