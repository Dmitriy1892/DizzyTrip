package com.coldfier.feature_bookmarks.ui.mvi.processors

import com.coldfier.core_mvi.SideEffectProducer
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksAction
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksSideEffect
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksState
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