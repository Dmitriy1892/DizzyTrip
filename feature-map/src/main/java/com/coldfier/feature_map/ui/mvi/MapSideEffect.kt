package com.coldfier.feature_map.ui.mvi

internal sealed interface MapSideEffect {
    class ShowErrorDialog(val error: Throwable) : MapSideEffect
    object InitMap : MapSideEffect
}