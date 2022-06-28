package com.coldfier.feature_countries.ui.country_detail

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
}

data class CountryDetailScreenState(
    val country: Country,
    val imageUriList: List<Uri>
)