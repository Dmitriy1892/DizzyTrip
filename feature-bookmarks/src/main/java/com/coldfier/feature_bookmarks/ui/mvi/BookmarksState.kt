package com.coldfier.feature_bookmarks.ui.mvi

import com.coldfier.core_data.repository.models.CountryShort

internal data class BookmarksState(
    val isShowProgress: Boolean = true,
    val countryShortList: List<CountryShort> = listOf()
)