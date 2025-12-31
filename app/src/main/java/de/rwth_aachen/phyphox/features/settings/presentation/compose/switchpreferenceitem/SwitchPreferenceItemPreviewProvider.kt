package de.rwth_aachen.phyphox.features.settings.presentation.compose.switchpreferenceitem

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState

class SwitchPreferenceItemPreviewProvider : PreviewParameterProvider<ResourceState<Boolean>> {
    override val values: Sequence<ResourceState<Boolean>>
        get() = sequenceOf(
            ResourceState.Loading,
            ResourceState.Success(true),
            ResourceState.Success(false),
        )
}
