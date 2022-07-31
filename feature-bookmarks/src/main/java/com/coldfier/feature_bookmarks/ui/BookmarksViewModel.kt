package com.coldfier.feature_bookmarks.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.coldfier.feature_bookmarks.ui.mvi.processors.BookmarksStore
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksUiEvent
import javax.inject.Inject

internal class BookmarksViewModel @Inject constructor(
    private val bookmarksStore: BookmarksStore
) : ViewModel() {


    val bookmarksStateFlow = bookmarksStore.stateFlow
    val bookmarksSideEffectFlow = bookmarksStore.sideEffectFlow

    fun sendUiEvent(event: BookmarksUiEvent) {
        bookmarksStore.consumeEvent(event)
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return bookmarksStore.loadImageForCountry(countryName)
    }

    override fun onCleared() {
        super.onCleared()
        bookmarksStore.onCleared()
    }
}