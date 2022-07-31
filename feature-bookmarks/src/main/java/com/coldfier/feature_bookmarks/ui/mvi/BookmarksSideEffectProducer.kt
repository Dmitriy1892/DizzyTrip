package com.coldfier.feature_bookmarks.ui.mvi

import com.coldfier.core_mvi.SideEffectProducer
import javax.inject.Inject

internal class BookmarksSideEffectProducer @Inject constructor(
) : SideEffectProducer<BookmarksState, BookmarksAction, BookmarksSideEffect> {

    override fun invoke(state: BookmarksState, action: BookmarksAction): BookmarksSideEffect? {
        return when (action) {
            is BookmarksAction.NavigateToCountry ->
                BookmarksSideEffect.NavigateToDetailScreen(action.country)

            is BookmarksAction.Error -> BookmarksSideEffect.ShowErrorDialog(action.error)

            else -> null
        }
    }
}