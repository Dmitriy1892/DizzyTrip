package com.coldfier.feature_bookmarks.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coldfier.core_data.repository.models.Country
import com.coldfier.core_data.repository.models.CountryShort
import com.coldfier.core_utils.ui.launchInIOCoroutine
import com.coldfier.feature_bookmarks.use_cases.BookmarksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class BookmarksViewModel @Inject constructor(
    private val bookmarksUseCase: BookmarksUseCase
) : ViewModel() {

    private val _bookmarksScreenStateFlow = MutableStateFlow(BookmarksScreenState())
    val bookmarksScreenStateFlow: StateFlow<BookmarksScreenState>
        get() = _bookmarksScreenStateFlow.asStateFlow()

    init {
        launchInIOCoroutine {
            bookmarksUseCase.bookmarksFlow
                .onEach { list ->
                    _bookmarksScreenStateFlow.update {
                        it.copy(
                            isShowProgress = false,
                            countryShortList = list
                        )
                    }
                }
                .collect()
        }
    }

    fun sendAction(action: BookmarksScreenAction) {
        viewModelScope.launch(Dispatchers.Default) {
            when (action) {
                is BookmarksScreenAction.OpenCountryFullInfo -> {
                    action.countryShort.uri?.let { uri ->
                        _bookmarksScreenStateFlow.update { it.copy(isShowProgress = true) }
                        getCountryByUrl(uri)
                    }
                }

                is BookmarksScreenAction.ChangeIsBookmark -> {
                    withContext(Dispatchers.IO) {
                        bookmarksUseCase.changeIsBookmark(
                            countryName = action.countryShort.name ?: "",
                            isBookmark = !(action.countryShort.isAddedToBookmark ?: false)
                        )
                    }
                }

                is BookmarksScreenAction.NavigationComplete -> {
                    _bookmarksScreenStateFlow.update {
                        it.copy(navigationState = NavigationState.None)
                    }
                }

                is BookmarksScreenAction.ErrorDialogClosed -> {
                    _bookmarksScreenStateFlow.update { it.copy(errorDialogMessage = null) }
                }
            }
        }
    }

    private fun getCountryByUrl(uri: Uri) {
        launchInIOCoroutine {
            try {
                val country = bookmarksUseCase.getCountryByUri(uri)

                _bookmarksScreenStateFlow.update {
                    it.copy(
                        isShowProgress = false,
                        navigationState = NavigationState.CountryDetailScreen(country)
                    )
                }
            } catch (e: Exception) {
                _bookmarksScreenStateFlow.update {
                    it.copy(
                        isShowProgress = false,
                        errorDialogMessage = e.message.toString(),
                        navigationState = NavigationState.None
                    )
                }
            }
        }
    }

    suspend fun loadImageForCountry(countryName: String): Uri? {
        return bookmarksUseCase.searchImageByCountryName(countryName)
    }
}

internal data class BookmarksScreenState(
    val isShowProgress: Boolean = true,
    val countryShortList: List<CountryShort> = listOf(),
    val errorDialogMessage: String? = null,
    val navigationState: NavigationState = NavigationState.None
)

internal sealed interface NavigationState {
    object None : NavigationState
    class CountryDetailScreen(val country: Country) : NavigationState
}

internal sealed interface BookmarksScreenAction {
    class OpenCountryFullInfo(val countryShort: CountryShort) : BookmarksScreenAction
    class ChangeIsBookmark(val countryShort: CountryShort) : BookmarksScreenAction
    object NavigationComplete : BookmarksScreenAction
    object ErrorDialogClosed : BookmarksScreenAction
}