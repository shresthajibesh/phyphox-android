package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode

import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem.SegmentedButtonUiModel
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.SettingsDelegate
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface AppUiModeDelegate : SettingsDelegate {
    val appUiModeFlow: Flow<UiResourceState<List<SegmentedButtonUiModel<AppUiMode>>>>

    suspend fun updateAppUiMode(appUiMode: SegmentedButtonUiModel<AppUiMode>)
}
