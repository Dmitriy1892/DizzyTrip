package com.coldfier.feature_bookmarks.ui.mvi

import com.coldfier.core_data.repository.models.Country

internal sealed interface BookmarksSideEffect {
    class NavigateToDetailScreen(val country: Country) : BookmarksSideEffect
    class ShowErrorDialog(val error: Throwable) : BookmarksSideEffect
}