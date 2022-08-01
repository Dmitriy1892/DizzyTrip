package com.coldfier.feature_countries.ui.mvi.processors

import android.net.Uri
import com.coldfier.core_mvi.store.FullStore
import com.coldfier.feature_countries.ui.mvi.CountriesAction
import com.coldfier.feature_countries.ui.mvi.CountriesSideEffect
import com.coldfier.feature_countries.ui.mvi.CountriesState
import com.coldfier.feature_countries.ui.mvi.CountriesUiEvent
import com.coldfier.feature_countries.use_cases.CountriesMiddleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class CountriesStore @Inject constructor(
    initialState: CountriesState,
    override val reducer: CountriesReducer,
    override val middleware: CountriesMiddleware,
    override val sideEffectProducer: CountriesSideEffectProducer
) : FullStore<CountriesState, CountriesUiEvent, CountriesAction, CountriesSideEffect>() {

    override val _stateFlow = MutableStateFlow(initialState)

    init {
        storeCoroutineScope.launch {
            middleware.countryShortsFlow
                .onStart { actionSharedFlow.emit(CountriesAction.SkeletonsLoading) }
                .catch { actionSharedFlow.emit(CountriesAction.Error(it)) }
                .onEach { actionSharedFlow.emit(CountriesAction.CountriesListLoaded(it)) }
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    suspend fun loadImageByCountryName(countryName: String): Uri? {
        return middleware.searchImageByCountryName(countryName)
    }

    override fun mapEventToAction(event: CountriesUiEvent): CountriesAction? {
        return when (event) {

            is CountriesUiEvent.OpenSearchedCountry ->
                CountriesAction.NavigateToDetailScreen(event.country)

            else -> null
        }
    }
}