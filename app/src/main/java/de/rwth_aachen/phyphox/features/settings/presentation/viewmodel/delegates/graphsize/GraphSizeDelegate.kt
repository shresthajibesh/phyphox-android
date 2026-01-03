package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.graphsize

import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarConfig
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface GraphSizeDelegate {
    val graphSizeFlow: Flow<UiResourceState<SeekBarConfig>>
    fun start(scope: CoroutineScope)
    suspend fun updateGraphSize(size: Float)
}
