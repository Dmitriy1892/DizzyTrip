package com.coldfier.feature_country_detail.ui.mvi.processors

import com.coldfier.core_mvi.Reducer
import com.coldfier.feature_country_detail.ui.mvi.CountryState
import com.coldfier.feature_country_detail.ui.mvi.CountryUiEvent
import javax.inject.Inject

internal class CountryReducer @Inject constructor() : Reducer<CountryState, CountryUiEvent> {
    override fun invoke(state: CountryState, action: CountryUiEvent): CountryState {
        return when (action) {

            is CountryUiEvent.ChangeIsBookmark -> state.copy(
                country = state.country.copy(isAddedToBookmark = action.isNeedToAddBookmark)
            )

            is CountryUiEvent.BookmarkStatus -> state.copy(
                country = state.country.copy(isAddedToBookmark = action.isBookmark)
            )

            is CountryUiEvent.CheckCountryIsBookmark -> state

            is CountryUiEvent.DeniedPermissions ->
                state.copy(deniedPermissions = action.deniedPermissions)

            is CountryUiEvent.GrantedPermissions -> state.copy(deniedPermissions = emptySet())
        }
    }
}