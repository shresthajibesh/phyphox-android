package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode

import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow

interface AppUiModeDelegate {
    val appUiModeFlow: Flow<UiResourceState<List<AppUiModeState>>>
}
