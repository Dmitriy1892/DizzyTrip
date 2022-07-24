package com.coldfier.feature_bookmarks

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.Dependencies

interface BookmarksDeps : Dependencies {

    fun navigateToDetailScreen(country: Country)
}