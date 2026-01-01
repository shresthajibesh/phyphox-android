package de.rwth_aachen.phyphox.features.settings.presentation.compose.preferencesummaryitem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import de.rwth_aachen.phyphox.ui.string.LoremIpsumStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme


@Composable
fun PreferenceSummaryItem(
    modifier: Modifier = Modifier,
    text: StringUIModel,
) {
    Text(
        modifier = modifier,
        text = text.resolve(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@PreviewLightDark
@Composable
internal fun PreferenceSummaryItemPreview() {
    PhyphoxTheme {
        PreferenceSummaryItem(
            text = LoremIpsumStringUIModel(4),
        )
    }
}
