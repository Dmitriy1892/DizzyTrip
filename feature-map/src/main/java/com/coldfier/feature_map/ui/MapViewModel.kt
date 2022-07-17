package com.coldfier.feature_map.ui

import android.Manifest
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_map.use_cases.MapUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MapViewModel @Inject constructor(
    private val mapUseCase: MapUseCase
) : ViewModel() {

    private val _mapScreenStateFlow = MutableStateFlow(MapScreenState())
    val mapScreenState: StateFlow<MapScreenState>
        get() = _mapScreenStateFlow.asStateFlow()

    init {
        launchInIOCoroutine {
            mapUseCase.countryShortsFlow
                .onStart {
                    _mapScreenStateFlow.update { it.copy(isShowLoading = true) }
                }
                .catch {
                    _mapScreenStateFlow.update {
                        it.copy(
                            isShowLoading = false,
                            isShowNoDataLoaded = true
                        )
                    }
                }
                .collect { countries ->
                    _mapScreenStateFlow.update {
                        it.copy(
                            isShowLoading = false,
                            isShowNoDataLoaded = false,
                            countryList = countries
                        )
                    }
                }
        }

    }

    fun sendAction(action: MapScreenAction) {
        viewModelScope.launch(Dispatchers.Default) {
            when (action) {
                is MapScreenAction.PermissionsGranted -> {
                    _mapScreenStateFlow.update {
                        it.copy(
                            deniedPermissions = setOf(),
                            isNeedToInitMap = true
                        )
                    }
                }

                is MapScreenAction.PermissionsDenied -> {
                    _mapScreenStateFlow.update { it.copy(deniedPermissions = action.deniedPermissions) }
                }

                is MapScreenAction.MapInitialized -> {
                    _mapScreenStateFlow.update { it.copy(isNeedToInitMap = false) }
                }

                is MapScreenAction.CountryChosen -> {
                    withContext(Dispatchers.IO) {
                        action.country.uri?.let { uri ->
                            _mapScreenStateFlow.update { it.copy(isShowCountrySearchLoading = true) }

                            try {
                                val country = mapUseCase.getCountryByUri(uri)
                                _mapScreenStateFlow.update {
                                    it.copy(
                                        isShowCountrySearchLoading = false,
                                        chosenCountry = country,
                                    )
                                }
                            } catch (e: Exception) {
                                _mapScreenStateFlow.update {
                                    it.copy(
                                        isShowCountrySearchLoading = true,
                                        errorDialogMessage = e.message.toString()
                                    )
                                }
                            }
                        }
                    }
                }

                is MapScreenAction.ErrorDialogClosed -> {
                    _mapScreenStateFlow.update {
                        it.copy(errorDialogMessage = null)
                    }
                }

                is MapScreenAction.ShowSearchLoadingState -> {
                    _mapScreenStateFlow.update {
                        it.copy(
                            searchRequest = action.searchRequest,
                            searchResult = SearchResult.Loading
                        )
                    }
                }

                is MapScreenAction.SetEmptySearchRequest -> {
                    _mapScreenStateFlow.update { it.copy(searchRequest = "") }
                }

                is MapScreenAction.SearchCountryByName -> {
                    searchCountry(action.countryName)
                }
            }
        }
    }

    private fun searchCountry(countryName: String) {
        launchInIOCoroutine {
            try {
                if (countryName.isNotBlank()) {
                    _mapScreenStateFlow.update {
                        it.copy(
                            searchRequest = countryName,
                            searchResult = SearchResult.Loading
                        )
                    }

                    val country = _mapScreenStateFlow.value.countryList.find {
                        it.name?.lowercase()!!.contains(countryName.lowercase())
                    }

                    val result = mapUseCase.getCountryByUri(country!!.uri!!)

                    _mapScreenStateFlow.update {
                        it.copy(
                            searchResult = SearchResult.Complete(result)
                        )
                    }
                } else {
                    _mapScreenStateFlow.update { it.copy(searchResult = null) }
                }
            } catch (e: Exception) {
                _mapScreenStateFlow.update {
                    it.copy(searchResult = SearchResult.Error)
                }
            }
        }
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return mapUseCase.searchImageByCountryName(countryName)
    }
}

internal data class MapScreenState(
    val isShowLoading: Boolean = false,
    val isShowNoDataLoaded: Boolean = false,
    val countryList: List<CountryShort> = listOf(),
    val chosenCountry: Country? = null,
    val isShowCountrySearchLoading: Boolean = false,
    val deniedPermissions: Set<String> = setOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    ),
    val isNeedToInitMap: Boolean = true,
    val errorDialogMessage: String? = null,

    val searchRequest: String = "",
    val searchResult: SearchResult? = null
)

internal sealed interface SearchResult {
    object Loading : SearchResult
    class Complete(val searchResult: Country) : SearchResult
    object Error : SearchResult
}

internal sealed interface MapScreenAction {
    object PermissionsGranted : MapScreenAction
    class PermissionsDenied(val deniedPermissions: Set<String>) : MapScreenAction
    object MapInitialized : MapScreenAction
    class CountryChosen(val country: CountryShort) : MapScreenAction
    object ErrorDialogClosed : MapScreenAction

    class ShowSearchLoadingState(val searchRequest: String) : MapScreenAction
    object SetEmptySearchRequest : MapScreenAction
    class SearchCountryByName(val countryName: String) : MapScreenAction
}