package de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SeekBarConfig
import de.rwth_aachen.phyphox.ui.skeleton
import de.rwth_aachen.phyphox.ui.string.LoremIpsumStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@Composable
fun SeekBarPreferenceItem(
    title: StringUIModel,
    seekBarConfig: ResourceState<SeekBarConfig>,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(text = title.resolve(), style = MaterialTheme.typography.bodyLarge)

        when (seekBarConfig) {
            ResourceState.Loading -> Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(16.dp)
                    .skeleton(),
            )

            is ResourceState.Success<SeekBarConfig> -> Slider(
                value = seekBarConfig.data.currentSize,
                onValueChange = onValueChange,
                valueRange = seekBarConfig.data.range,
                steps = (seekBarConfig.data.range.endInclusive - seekBarConfig.data.range.start).toInt() - 1,
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
internal fun SeekBarPreferenceItemPreview(
    @PreviewParameter(SeekBarPreferenceItemPreviewProvider::class) graphSize: ResourceState<SeekBarConfig>,
) {
    PhyphoxTheme {
        Surface {
            SeekBarPreferenceItem(
                title = LoremIpsumStringUIModel(2),
                seekBarConfig = graphSize,
                onValueChange = {},
            )
        }
    }
}
