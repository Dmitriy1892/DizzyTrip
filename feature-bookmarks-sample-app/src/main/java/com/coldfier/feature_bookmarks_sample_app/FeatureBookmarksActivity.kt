package com.coldfier.feature_bookmarks_sample_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.DepsMap
import com.coldfier.core_utils.di.HasDependencies
import com.coldfier.feature_bookmarks.BookmarksDeps
import com.coldfier.feature_country_detail.CountryDetailDeps
import com.coldfier.feature_country_detail.ui.CountryDetailFragment

class FeatureBookmarksActivity : AppCompatActivity(), HasDependencies {

    private val countryDetailDeps = object : CountryDetailDeps {
        override var country: Country = Country()
    }

    private val bookmarksDeps = object : BookmarksDeps {
        override fun navigateToDetailScreen(country: Country) {
            countryDetailDeps.country = country
            findNavController(R.id.container)
                .navigate(R.id.action_bookmarksFragment_to_countryDetailFragment)
        }
    }

    override val depsMap: DepsMap = mapOf(
        BookmarksDeps::class.java to bookmarksDeps,
        CountryDetailDeps::class.java to countryDetailDeps
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_bookmarks)
    }
}