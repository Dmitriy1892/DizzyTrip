package com.coldfier.feature_countries.ui.mvi.processors

import com.coldfier.core_mvi.SideEffectProducer
import com.coldfier.feature_countries.ui.mvi.CountriesAction
import com.coldfier.feature_countries.ui.mvi.CountriesSideEffect
import com.coldfier.feature_countries.ui.mvi.CountriesState
import javax.inject.Inject

internal class CountriesSideEffectProducer @Inject constructor(

) : SideEffectProducer<CountriesState, CountriesAction, CountriesSideEffect> {

    override fun invoke(state: CountriesState, action: CountriesAction): CountriesSideEffect? {
        return when (action) {

            is CountriesAction.NavigateToDetailScreen ->
                CountriesSideEffect.NavigateToDetailScreen(action.country)

            is CountriesAction.Error -> CountriesSideEffect.ShowErrorDialog(action.error)

            else -> null
        }
    }
}