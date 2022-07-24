package com.coldfier.feature_bookmarks.di

import android.content.Context
import com.coldfier.core_data.CoreDataDeps
import com.coldfier.feature_bookmarks.BookmarksDeps
import com.coldfier.feature_bookmarks.di.modules.BookmarksModule
import com.coldfier.feature_bookmarks.di.modules.ViewModelsModule
import com.coldfier.feature_bookmarks.ui.BookmarksFragment
import dagger.BindsInstance
import dagger.Component

@BookmarksScope
@Component(
    modules = [BookmarksModule::class, ViewModelsModule::class],
    dependencies = [BookmarksDeps::class]
)
internal interface BookmarksComponent : CoreDataDeps {

    override val context: Context

    fun inject(bookmarksFragment: BookmarksFragment)

    @Component.Builder
    interface Builder {

        fun deps(bookmarksDeps: BookmarksDeps): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): BookmarksComponent
    }
}