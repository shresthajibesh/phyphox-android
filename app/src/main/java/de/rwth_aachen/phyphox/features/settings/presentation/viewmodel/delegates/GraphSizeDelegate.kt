package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates

import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SeekBarConfig
import kotlinx.coroutines.flow.StateFlow

interface GraphSizeDelegate {
    val graphSizeStateFlow: StateFlow<ResourceState<SeekBarConfig>>
}

