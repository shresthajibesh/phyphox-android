package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import de.rwth_aachen.phyphox.ui.string.StringUIModel

sealed interface SettingsSheetUiModel {
    data class AccessPortSheetUiModel(
        val currentPort: StringUIModel,
        val range: ClosedFloatingPointRange<Int>,
        val error: StringUIModel? = null,
    ) : SettingsSheetUiModel
}
