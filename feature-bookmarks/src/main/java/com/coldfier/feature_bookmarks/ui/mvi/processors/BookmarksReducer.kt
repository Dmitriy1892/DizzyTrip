package com.coldfier.feature_bookmarks.ui.mvi.processors

import com.coldfier.core_mvi.Reducer
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksAction
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksState
import javax.inject.Inject

internal class BookmarksReducer @Inject constructor(): Reducer<BookmarksState, BookmarksAction> {

    override fun invoke(state: BookmarksState, action: BookmarksAction): BookmarksState {
        return when (action) {
            is BookmarksAction.Loading -> state.copy(isShowProgress = true)

            is BookmarksAction.CountriesListLoaded ->
                state.copy(isShowProgress = false, countryShortList = action.countries)

            is BookmarksAction.NavigateToCountry -> state.copy(isShowProgress = false)

            is BookmarksAction.Error -> state.copy(isShowProgress = false)
        }
    }
}