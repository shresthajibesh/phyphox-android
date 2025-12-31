package de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SeekBarConfig
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState

class SeekBarPreferenceItemPreviewProvider : PreviewParameterProvider<ResourceState<SeekBarConfig>> {
    override val values: Sequence<ResourceState<SeekBarConfig>>
        get() = sequenceOf(
            ResourceState.Loading,
            ResourceState.Success(
                SeekBarConfig(
                    currentSize = 1f,
                    minSize = 0f,
                    maxSize = 3f,
                ),
            ),
        )
}
