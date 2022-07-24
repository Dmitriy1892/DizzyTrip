package com.coldfier.feature_country_detail

import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.di.Dependencies

interface CountryDetailDeps : Dependencies {
    var country: Country
}