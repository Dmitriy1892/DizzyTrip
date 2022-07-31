package com.coldfier.feature_bookmarks.ui.mvi

import android.net.Uri
import com.coldfier.core_mvi.store.FullStore
import com.coldfier.feature_bookmarks.use_cases.BookmarksMiddleware
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class BookmarksStore @Inject constructor(
    initialState: BookmarksState,
    override val middleware: BookmarksMiddleware,
    override val reducer: BookmarksReducer,
    override val sideEffectProducer: BookmarksSideEffectProducer
): FullStore<BookmarksState, BookmarksUiEvent, BookmarksAction, BookmarksSideEffect>() {

    override val _stateFlow = MutableStateFlow(initialState)

    init {
        storeCoroutineScope.launch {
            middleware.bookmarksFlow
                .flowOn(Dispatchers.IO)
                .onStart { actionSharedFlow.emit(BookmarksAction.Loading) }
                .catch { actionSharedFlow.emit(BookmarksAction.Error(it)) }
                .onEach { actionSharedFlow.emit(BookmarksAction.CountriesListLoaded(it)) }
                .flowOn(Dispatchers.Main)
                .collect()
        }
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return middleware.searchImageByCountryName(countryName)
    }

    override fun mapEventToAction(event: BookmarksUiEvent): BookmarksAction? {
        return null
    }
}