package com.coldfier.feature_bookmarks.di.modules

import androidx.lifecycle.ViewModel
import com.coldfier.core_utils.di.ViewModelKey
import com.coldfier.feature_bookmarks.ui.BookmarksViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(BookmarksViewModel::class)
    fun bindBookmarksViewModel(bookmarksViewModel: BookmarksViewModel): ViewModel
}