package com.coldfier.feature_countries.ui.country_detail

import android.Manifest
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coldfier.core_data.repository.models.Country
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CountryDetailViewModel constructor(country: Country) : ViewModel() {

    class CountryDetailViewModelFactory @AssistedInject constructor(
        @Assisted("country") private val country: Country
    ): ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CountryDetailViewModel(country) as T
        }

        @AssistedFactory
        interface CountryDetailViewModelAssistedFactory {
            fun create(@Assisted("country") country: Country): CountryDetailViewModelFactory
        }
    }

    private val _screenStateFlow = MutableStateFlow(CountryDetailScreenState(country, listOf()))
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
    val imageUriList: List<Uri>,
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