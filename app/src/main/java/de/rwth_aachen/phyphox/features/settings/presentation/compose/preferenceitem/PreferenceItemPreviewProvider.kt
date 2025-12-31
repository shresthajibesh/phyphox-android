package de.rwth_aachen.phyphox.features.settings.presentation.compose.preferenceitem

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SeekBarConfig
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.ui.string.LoremIpsumStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel

class PreferenceItemPreviewProvider : PreviewParameterProvider<ResourceState<StringUIModel>> {
    override val values: Sequence<ResourceState<StringUIModel>>
        get() = sequenceOf(
            ResourceState.Loading,
            ResourceState.Success(LoremIpsumStringUIModel(8)),
        )
}
