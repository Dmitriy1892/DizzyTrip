package com.coldfier.feature_bookmarks.use_cases

import android.net.Uri
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.repositories.CountriesRepository
import com.coldfier.core_data.repository.repositories.PixabayImagesRepository
import com.coldfier.core_mvi.Middleware
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksAction
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksState
import com.coldfier.feature_bookmarks.ui.mvi.BookmarksUiEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class BookmarksMiddleware @Inject constructor(
    private val countriesRepository: CountriesRepository,
    private val pixabayImagesRepository: PixabayImagesRepository
) : Middleware<BookmarksState, BookmarksUiEvent, BookmarksAction>() {

    val bookmarksFlow = countriesRepository.bookmarksFlow
        .onStart { BookmarksAction.Loading }
        .catch { BookmarksAction.Error(it) }
        .onEach { BookmarksAction.CountriesListLoaded(it) }

    override fun invoke(state: BookmarksState, event: BookmarksUiEvent): Flow<BookmarksAction>? {
        return when (event) {
            is BookmarksUiEvent.ChangeIsBookmark -> {
                middlewareCoroutineScope.launch {
                    changeIsBookmark(
                        countryName = event.countryShort.name ?: "",
                        isBookmark = event.countryShort.isAddedToBookmark != true
                    )
                }
                null
            }

            is BookmarksUiEvent.OpenCountryFullInfo -> {
                return flow {
                    emit(BookmarksAction.Loading)
                    try {
                        val country =
                            getCountryByUri(event.countryShort.uri ?: Uri.parse(""))

                        emit(BookmarksAction.NavigateToCountry(country))
                    } catch (e: Throwable) {
                        emit(BookmarksAction.Error(e))
                    }
                }
            }
        }
    }

    private suspend fun getCountryByUri(uri: Uri): Country =
        countriesRepository.getCountryByUri(uri)

    suspend fun searchImageByCountryName(countryName: String): Uri? {
        return pixabayImagesRepository.searchImageByCountryName(countryName)
    }

    private suspend fun changeIsBookmark(countryName: String, isBookmark: Boolean) {
        countriesRepository.updateIsBookmark(countryName, isBookmark)
    }
}