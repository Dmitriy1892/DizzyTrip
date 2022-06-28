package com.coldfier.feature_countries.ui.country_detail

import android.Manifest
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_countries.use_cases.CountryDetailUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CountryDetailViewModel constructor(
    country: Country,
    private val countryDetailUseCase: CountryDetailUseCase
) : ViewModel() {

    class CountryDetailViewModelFactory @AssistedInject constructor(
        @Assisted("country") private val country: Country,
        private val countryDetailUseCase: CountryDetailUseCase
    ): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CountryDetailViewModel(country, countryDetailUseCase) as T
        }

        @AssistedFactory
        interface VMAssistedFactory {
            fun create(@Assisted("country") country: Country): CountryDetailViewModelFactory
        }
    }

    init {
        launchInIOCoroutine {
            val imageUriList =
                countryDetailUseCase.searchImagesByCountryName(country.name ?: "")
            _screenStateFlow.value = _screenStateFlow.value.copy(imageUriList = imageUriList)
        }
    }

    private val _screenStateFlow = MutableStateFlow(CountryDetailScreenState(country))
    val screenStateFlow: StateFlow<CountryDetailScreenState>
        get() = _screenStateFlow.asStateFlow()


    fun sendAction(action: CountryDetailScreenAction) {
        when (action) {
            is CountryDetailScreenAction.DeniedPermissions -> {
                _screenStateFlow.value = _screenStateFlow.value.copy(
                    deniedPermissions = action.deniedPermissions
                )
            }

            is CountryDetailScreenAction.GrantedPermissions -> {
                _screenStateFlow.value = _screenStateFlow.value.copy(
                    deniedPermissions = setOf()
                )
            }
        }
    }
}

data class CountryDetailScreenState(
    val country: Country,
    val imageUriList: List<Uri> = listOf(Uri.EMPTY),
    val deniedPermissions: Set<String> = setOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    )
)

sealed interface CountryDetailScreenAction {
    class DeniedPermissions(val deniedPermissions: Set<String>) : CountryDetailScreenAction
    object GrantedPermissions : CountryDetailScreenAction
}