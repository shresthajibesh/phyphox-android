package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarConfig
import de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem.SegmentedButtonUiModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState

data class SettingsUiState(
    val currentLanguage: UiResourceState<StringUIModel> = UiResourceState.Loading,
    val graphSize: UiResourceState<SeekBarConfig> = UiResourceState.Loading,
    val appUiMode: UiResourceState<List<SegmentedButtonUiModel<AppUiMode>>> = UiResourceState.Loading,
//    val dynamicTheme: ResourceState<Boolean> = ResourceState.Loading,
    val accessPort: UiResourceState<StringUIModel> = UiResourceState.Loading,
    val proximityLockEnabled: UiResourceState<Boolean> = UiResourceState.Loading,
    val modal: SettingsSheetUiModel? = null,
)
