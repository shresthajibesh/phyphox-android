package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates

import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import kotlinx.coroutines.flow.StateFlow

interface UiModeDelegate {
    val uiModeStateFlow: StateFlow<ResourceState<List<AppUiMode>>>
}

