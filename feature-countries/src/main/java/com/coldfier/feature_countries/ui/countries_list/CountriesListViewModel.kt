package com.coldfier.feature_countries.ui.countries_list

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coldfier.core_data.domain.models.CountryShort
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_countries.use_cases.CountriesListUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class CountriesListViewModel @Inject constructor(
    private val countriesListUseCase: CountriesListUseCase
): ViewModel() {

    private var callback: StateCallback? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val countriesScreenStateFlow = callbackFlow<CountriesScreenState> {
        callback = object : StateCallback {
            override fun setScreenState(state: CountriesScreenState) { trySend(state) }
        }

        countriesListUseCase.countryShortsFlow
            .catch { error -> send(CountriesScreenState.Error(error.message.toString())) }
            .collect { send(CountriesScreenState.Complete(it)) }

        awaitClose { callback = null }
    }.stateIn(viewModelScope, SharingStarted.Lazily, CountriesScreenState.Loading)

    private val navigateChannel = Channel<Boolean>()
    val navigateFlow: Flow<Boolean>
        get() = navigateChannel.receiveAsFlow()

    fun sendEvent(countriesScreenEvent: CountriesScreenEvent) {
        launchInIOCoroutine {
            when (countriesScreenEvent) {
                is CountriesScreenEvent.OpenCountryFullInfo -> {
                    countriesScreenEvent.countryShort.uri?.let {
                        try {
                            navigateChannel.send(true)
                            countriesListUseCase.getCountryByUri(it)
                        } catch (e: Exception) {
                            callback?.setScreenState(
                                CountriesScreenState.Error(e.message.toString())
                            )
                        }
                    }
                }

                is CountriesScreenEvent.ChangeIsBookmark -> {

                }
            }
        }
    }

    private fun getCountryByUri(uri: Uri) {
        launchInIOCoroutine {
            countriesListUseCase.getCountryByUri(uri)
        }
    }

    private fun searchCountry(countryName: String) {
        launchInIOCoroutine {
            countriesListUseCase.searchCountry(countryName)
        }
    }
}

private interface StateCallback {
    fun setScreenState(state: CountriesScreenState)
}

internal sealed interface CountriesScreenState {
    object Loading : CountriesScreenState

    class Complete(
        val countryShortList: List<CountryShort>
    ) : CountriesScreenState

    class Error(val message: String) : CountriesScreenState
}

internal sealed interface CountriesScreenEvent {
    class OpenCountryFullInfo(val countryShort: CountryShort) : CountriesScreenEvent
    class ChangeIsBookmark(val countryShort: CountryShort) : CountriesScreenEvent
}