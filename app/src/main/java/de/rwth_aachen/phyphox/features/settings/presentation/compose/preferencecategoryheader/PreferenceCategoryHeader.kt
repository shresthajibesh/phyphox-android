package de.rwth_aachen.phyphox.features.settings.presentation.compose.preferencecategoryheader

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@Composable
fun PreferenceCategoryHeader(
    modifier: Modifier = Modifier,
    title: StringUIModel,
) {
    Text(
        text = title.resolve(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
    )
}

@Preview(showBackground = true)
@Composable
internal fun SeekBarPreferenceItemPreview(
    @PreviewParameter(PreferenceCategoryHeaderPreviewProvider::class) preview: StringUIModel,
) {
    PhyphoxTheme {
        Surface {
            PreferenceCategoryHeader(
                title = preview,
            )
        }
    }
}
