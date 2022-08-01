package com.coldfier.feature_map.ui.mvi.processors

import com.coldfier.core_mvi.SideEffectProducer
import com.coldfier.feature_map.ui.mvi.MapAction
import com.coldfier.feature_map.ui.mvi.MapSideEffect
import com.coldfier.feature_map.ui.mvi.MapState
import javax.inject.Inject

internal class MapSideEffectProducer @Inject constructor(
) : SideEffectProducer<MapState, MapAction, MapSideEffect> {

    override fun invoke(state: MapState, action: MapAction): MapSideEffect? {
        return when (action) {

            is MapAction.PermissionsGranted -> MapSideEffect.InitMap

            is MapAction.Error -> MapSideEffect.ShowErrorDialog(action.error)

            else -> null
        }
    }
}