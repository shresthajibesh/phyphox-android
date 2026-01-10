package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.graphsize

import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarConfig
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.SettingsDelegate
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface GraphSizeDelegate  : SettingsDelegate{
    val graphSizeFlow: Flow<UiResourceState<SeekBarConfig>>
    suspend fun updateGraphSize(size: Float)
}
