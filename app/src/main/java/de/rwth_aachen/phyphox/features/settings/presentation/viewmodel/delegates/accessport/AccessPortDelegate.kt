package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport

import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.SettingsDelegate
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow

interface AccessPortDelegate : SettingsDelegate {
    val accessPortFlow: Flow<UiResourceState<StringUIModel>>
    val portInputModal: Flow<AccessPortSheetUiModel??>

    suspend fun showAccessPortInputModal()

    suspend fun setAccessPort(newPort: String)
    fun dismissModal()
}

