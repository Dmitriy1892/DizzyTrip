package com.coldfier.feature_countries.ui.countries_list

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_countries.use_cases.CountriesListUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
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
            .collect {
                delay(2000)
                send(CountriesScreenState.Complete(it))
            }

        awaitClose { callback = null }
    }.stateIn(viewModelScope, SharingStarted.Lazily, CountriesScreenState.Loading)

    private val navigateChannel = Channel<Country>()
    val navigateFlow: Flow<Country>
        get() = navigateChannel.receiveAsFlow()

    fun sendEvent(countriesScreenEvent: CountriesScreenEvent) {
        launchInIOCoroutine {
            when (countriesScreenEvent) {
                is CountriesScreenEvent.OpenCountryFullInfo -> {
                    countriesScreenEvent.countryShort.uri?.let {
                        try {
                            navigateChannel.send(countriesListUseCase.getCountryByUri(it))
                        } catch (e: Exception) {
                            val searchResult = when (val state = countriesScreenStateFlow.value) {
                                is CountriesScreenState.Loading -> null
                                is CountriesScreenState.Complete -> state.searchResult
                                is CountriesScreenState.Error -> state.searchResult
                            }

                            callback?.setScreenState(
                                CountriesScreenState.Error(e.message.toString(), searchResult)
                            )
                        }
                    }
                }

                is CountriesScreenEvent.OpenCountry -> {
                    countriesScreenEvent.country // TODO - NAVIGATE
                }

                is CountriesScreenEvent.ChangeIsBookmark -> {

                }

                is CountriesScreenEvent.OpenUserProfile -> {

                }

                is CountriesScreenEvent.SearchCountryByName -> {
                    searchCountry(countriesScreenEvent.countryName)
                }

                is CountriesScreenEvent.CountrySearchLoading -> {
                    val newState = updateStateWithSearchResult(SearchResult.Loading)
                    callback?.setScreenState(newState)
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
            val loadingState = updateStateWithSearchResult(SearchResult.Loading)
            callback?.setScreenState(loadingState)

            val result = try {
                if (countryName.isBlank()) {
                    null
                } else {
                    SearchResult.Complete(countriesListUseCase.searchCountry(countryName))
                }
            } catch (e: Exception) {
                SearchResult.Error(e.message.toString())
            }

            val newState = updateStateWithSearchResult(result)

            callback?.setScreenState(newState)
        }
    }

    private fun updateStateWithSearchResult(searchResult: SearchResult?): CountriesScreenState {
        return when (val currentState = countriesScreenStateFlow.value) {
            is CountriesScreenState.Loading -> {
                CountriesScreenState.Loading
            }

            is CountriesScreenState.Complete -> {
                CountriesScreenState.Complete(currentState.countryShortList, searchResult)
            }

            is CountriesScreenState.Error -> {
                CountriesScreenState.Error(currentState.message, searchResult)
            }
        }
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return countriesListUseCase.searchImageByCountryName(countryName)
    }
}

private interface StateCallback {
    fun setScreenState(state: CountriesScreenState)
}

internal sealed interface SearchResult {
    object Loading : SearchResult
    class Complete(val searchResult: Country) : SearchResult
    class Error(val message: String) : SearchResult
}

internal sealed interface CountriesScreenState {
    object Loading : CountriesScreenState

    class Complete(
        val countryShortList: List<CountryShort>,
        val searchResult: SearchResult? = null
    ) : CountriesScreenState

    class Error(
        val message: String,
        val searchResult: SearchResult? = null
    ) : CountriesScreenState
}

internal sealed interface CountriesScreenEvent {
    class OpenCountryFullInfo(val countryShort: CountryShort) : CountriesScreenEvent
    class OpenCountry(val country: Country) : CountriesScreenEvent
    class ChangeIsBookmark(val countryShort: CountryShort) : CountriesScreenEvent
    object OpenUserProfile : CountriesScreenEvent
    class SearchCountryByName(val countryName: String) : CountriesScreenEvent
    object CountrySearchLoading : CountriesScreenEvent
}