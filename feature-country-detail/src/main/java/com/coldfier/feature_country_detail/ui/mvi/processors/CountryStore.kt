package com.coldfier.feature_country_detail.ui.mvi.processors

import android.net.Uri
import com.coldfier.core_mvi.store.BaseStore
import com.coldfier.feature_country_detail.ui.mvi.CountryState
import com.coldfier.feature_country_detail.ui.mvi.CountryUiEvent
import com.coldfier.feature_country_detail.use_cases.CountryMiddleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class CountryStore @Inject constructor(
    initialState: CountryState,
    override val reducer: CountryReducer,
    override val middleware: CountryMiddleware
) : BaseStore<CountryState, CountryUiEvent>() {

    override val _stateFlow = MutableStateFlow(initialState)

    init {
        storeCoroutineScope.launch {
            val imageUriList = withContext(Dispatchers.IO) {
                try {
                    middleware.searchImagesByCountryName(initialState.country.name ?: "")
                } catch (e: Exception) {
                    listOf(Uri.EMPTY)
                }
            }

            _stateFlow.update { it.copy(imageUriList = imageUriList) }
        }
    }
}