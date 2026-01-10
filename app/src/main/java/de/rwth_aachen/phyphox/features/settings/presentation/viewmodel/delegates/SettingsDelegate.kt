package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates

import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SettingsSheetUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface SettingsDelegate {
    fun start(scope: CoroutineScope)
}
