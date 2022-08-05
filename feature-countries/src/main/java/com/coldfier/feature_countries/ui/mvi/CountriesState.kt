package com.coldfier.feature_countries.ui.mvi

import android.graphics.drawable.Drawable
import com.coldfier.core_data.repository.models.CountryShort

internal data class CountriesState(
    val isShowLoadingSkeleton: Boolean = true,
    val isShowProgress: Boolean = false,
    val userAvatar: Drawable? = null,
    val countryShortList: List<CountryShort> = listOf()
)