package com.coldfier.feature_countries.ui.countries_list

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_countries.use_cases.CountriesListUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class CountriesListViewModel @Inject constructor(
    private val countriesListUseCase: CountriesListUseCase
): ViewModel() {

    private val _countriesScreenStateFlow = MutableStateFlow(CountriesScreenState())
    val countriesScreenStateFlow: StateFlow<CountriesScreenState>
        get() = _countriesScreenStateFlow.asStateFlow()
    
    init {
        launchInIOCoroutine {
            countriesListUseCase.countryShortsFlow.catch {
                _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                    isShowLoadingSkeleton = false,
                    countryShortList = listOf()
                )
            }.collect { countryShortList ->
                delay(2000)
                _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                    isShowLoadingSkeleton = false,
                    countryShortList = countryShortList
                )
            }
        }
    }
    
    fun sendEvent(countriesScreenEvent: CountriesScreenEvent) {
        launchInIOCoroutine { 
            when (countriesScreenEvent) {
                is CountriesScreenEvent.OpenUserProfile -> {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        navigationState = NavigationState.UserProfileScreen
                    )
                }

                is CountriesScreenEvent.ShowSearchLoadingState -> {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        searchRequest = countriesScreenEvent.searchRequest,
                        searchResult = SearchResult.Loading
                    )
                }

                is CountriesScreenEvent.SetEmptySearchRequest -> {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        searchRequest = "",
                    )
                }

                is CountriesScreenEvent.SearchCountryByName -> {
                    searchCountry(countriesScreenEvent.countryName)
                }

                is CountriesScreenEvent.OpenSearchedCountry -> {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        navigationState = NavigationState.CountryDetailScreen(
                            countriesScreenEvent.country
                        )
                    )
                }

                is CountriesScreenEvent.OpenCountryFullInfo -> {
                    countriesScreenEvent.countryShort.uri?.let { uri ->
                        try {
                            _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                                navigationState = NavigationState.Loading
                            )

                            val country = countriesListUseCase.getCountryByUri(uri)

                            _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                                navigationState = NavigationState.CountryDetailScreen(country)
                            )
                        } catch (e: Exception) {
                            _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                                errorDialogMessage = e.message.toString(),
                                navigationState = NavigationState.None
                            )
                        }
                    }
                }

                is CountriesScreenEvent.ChangeIsBookmark -> {
                    // TODO
                }

                is CountriesScreenEvent.NavigationComplete -> {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        navigationState = NavigationState.None
                    )
                }

                is CountriesScreenEvent.ErrorDialogClosed -> {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        errorDialogMessage = null
                    )
                }
            }
        }
    }

    private fun searchCountry(countryName: String) {
        launchInIOCoroutine {

            try {
                if (countryName.isNotBlank()) {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        searchRequest = countryName,
                        searchResult = SearchResult.Loading
                    )

                    val country = countriesListUseCase.searchCountry(countryName)
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        searchResult = SearchResult.Complete(country)
                    )
                } else {
                    _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                        searchResult = null
                    )
                }
            } catch (e: Exception) {
                _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                    searchResult = SearchResult.Error(e.message.toString())
                )
            }
        }
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return countriesListUseCase.searchImageByCountryName(countryName)
    }
}

internal data class CountriesScreenState(
    val isShowLoadingSkeleton: Boolean = true,
    val userAvatar: Drawable? = null,
    val searchRequest: String = "",
    val searchResult: SearchResult? = null,
    val countryShortList: List<CountryShort> = listOf(),
    val errorDialogMessage: String? = null,
    val navigationState: NavigationState = NavigationState.None
)

internal sealed interface CountriesScreenEvent {
    object OpenUserProfile : CountriesScreenEvent
    class ShowSearchLoadingState(val searchRequest: String) : CountriesScreenEvent
    object SetEmptySearchRequest : CountriesScreenEvent
    class SearchCountryByName(val countryName: String) : CountriesScreenEvent
    class OpenSearchedCountry(val country: Country) : CountriesScreenEvent
    class OpenCountryFullInfo(val countryShort: CountryShort) : CountriesScreenEvent
    class ChangeIsBookmark(val countryShort: CountryShort) : CountriesScreenEvent
    object NavigationComplete : CountriesScreenEvent
    object ErrorDialogClosed : CountriesScreenEvent
}

internal sealed interface SearchResult {
    object Loading : SearchResult
    class Complete(val searchResult: Country) : SearchResult
    class Error(val message: String) : SearchResult
}

internal sealed interface NavigationState {
    object None : NavigationState
    object Loading : NavigationState
    object UserProfileScreen : NavigationState
    class CountryDetailScreen(val country: Country) : NavigationState
}