package de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem

import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.ui.string.StringUIModel

data class SegmentedButtonConfig<out T>(
    val item: T,
    val text: StringUIModel,
)

data class SegmentedButtonsPreferenceItemConfig<T>(
    val selectedItem: SegmentedButtonConfig<T>,
    val options: List<SegmentedButtonConfig<T>>,
)
