package com.coldfier.feature_countries.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import coil.size.Dimension
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_countries.use_cases.CountriesListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CountriesListViewModel @Inject constructor(
    private val countriesListUseCase: CountriesListUseCase
): ViewModel() {

    private val _countriesScreenStateFlow = MutableStateFlow(CountriesScreenState())
    val countriesScreenStateFlow: StateFlow<CountriesScreenState>
        get() = _countriesScreenStateFlow.asStateFlow()
    
    init {
        launchInIOCoroutine {
            countriesListUseCase.countryShortsFlow.catch { error ->
                Log.e("CountriesListViewModel", error.message.toString())
                _countriesScreenStateFlow.value = _countriesScreenStateFlow.value.copy(
                    isShowLoadingSkeleton = false,
                    countryShortList = listOf()
                )
            }.collect { countryShortList ->
                delay(2000)
                _countriesScreenStateFlow.update {
                    it.copy(
                        isShowLoadingSkeleton = false,
                        countryShortList = countryShortList
                    )
                }
            }
        }
    }
    
    fun sendEvent(countriesScreenEvent: CountriesScreenEvent) {
        launchInIOCoroutine { 
            when (countriesScreenEvent) {
                is CountriesScreenEvent.OpenUserProfile -> {
                    _countriesScreenStateFlow.update {
                        it.copy(navigationState = NavigationState.UserProfileScreen)
                    }
                }

                is CountriesScreenEvent.ShowSearchLoadingState -> {
                    _countriesScreenStateFlow.update {
                        it.copy(
                            searchRequest = countriesScreenEvent.searchRequest,
                            searchResult = SearchResult.Loading
                        )
                    }
                }

                is CountriesScreenEvent.SetEmptySearchRequest -> {
                    _countriesScreenStateFlow.update { it.copy(searchRequest = "") }
                }

                is CountriesScreenEvent.SearchCountryByName -> {
                    searchCountry(countriesScreenEvent.countryName)
                }

                is CountriesScreenEvent.OpenSearchedCountry -> {
                    _countriesScreenStateFlow.update {
                        it.copy(
                            navigationState =
                            NavigationState.CountryDetailScreen(countriesScreenEvent.country)
                        )
                    }
                }

                is CountriesScreenEvent.OpenCountryFullInfo -> {
                    countriesScreenEvent.countryShort.uri?.let { uri ->
                        try {
                            _countriesScreenStateFlow.update {
                                it.copy(navigationState = NavigationState.Loading)
                            }

                            val country = countriesListUseCase.getCountryByUri(uri)

                            _countriesScreenStateFlow.update {
                                it.copy(
                                    navigationState = NavigationState.CountryDetailScreen(country)
                                )
                            }
                        } catch (e: Exception) {
                            _countriesScreenStateFlow.update {
                                it.copy(
                                    errorDialogMessage = e.message.toString(),
                                    navigationState = NavigationState.None
                                )
                            }
                        }
                    }
                }

                is CountriesScreenEvent.ChangeIsBookmark -> {
                    withContext(Dispatchers.IO) {
                        countriesListUseCase.changeIsBookmark(
                            countryName = countriesScreenEvent.countryShort.name ?: "",
                            isBookmark = !(countriesScreenEvent.countryShort.isAddedToBookmark ?: false)
                        )
                    }
                }

                is CountriesScreenEvent.NavigationComplete -> {
                    _countriesScreenStateFlow.update {
                        it.copy(navigationState = NavigationState.None)
                    }
                }

                is CountriesScreenEvent.ErrorDialogClosed -> {
                    _countriesScreenStateFlow.update { it.copy(errorDialogMessage = null) }
                }
            }
        }
    }

    private fun searchCountry(countryName: String) {
        launchInIOCoroutine {
            try {
                if (countryName.isNotBlank()) {
                    _countriesScreenStateFlow.update {
                        it.copy(
                            searchRequest = countryName,
                            searchResult = SearchResult.Loading
                        )
                    }

                    val country = countriesListUseCase.searchCountry(countryName)
                    _countriesScreenStateFlow.update {
                        it.copy(searchResult = SearchResult.Complete(country))
                    }
                } else {
                    _countriesScreenStateFlow.update { it.copy(searchResult = null) }
                }
            } catch (e: Exception) {
                _countriesScreenStateFlow.update {
                    it.copy(searchResult = SearchResult.Error(e.message.toString()))
                }
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