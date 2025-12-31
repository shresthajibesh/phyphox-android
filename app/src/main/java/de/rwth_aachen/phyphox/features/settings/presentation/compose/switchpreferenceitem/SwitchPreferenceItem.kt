package de.rwth_aachen.phyphox.features.settings.presentation.compose.switchpreferenceitem

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.isChecked
import de.rwth_aachen.phyphox.ui.skeleton
import de.rwth_aachen.phyphox.ui.string.LoremIpsumStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@Composable
fun SwitchPreferenceItem(
    title: StringUIModel,
    summary: StringUIModel? = null,
    iconRes: Int? = null,
    checked: ResourceState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(checked is ResourceState.Success) {
                onCheckedChange(checked.isChecked())
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        iconRes?.let {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title.resolve(), style = MaterialTheme.typography.bodyLarge)
            summary?.let {
                Text(
                    text = summary.resolve(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.size(4.dp))
        when (checked) {
            ResourceState.Loading -> Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(48.dp)
                    .height(32.dp)
                    .skeleton(),
            )

            is ResourceState.Success<Boolean> ->
                Switch(
                    checked = checked.isChecked(),
                    onCheckedChange = onCheckedChange,
                )
        }

    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
internal fun SwitchPreferenceItemPreview(
    @PreviewParameter(SwitchPreferenceItemPreviewProvider::class) value: ResourceState<Boolean>,
) {
    PhyphoxTheme {
        Surface {
            SwitchPreferenceItem(
                title = LoremIpsumStringUIModel(2),
                summary = LoremIpsumStringUIModel(15),
                iconRes = R.drawable.ic_dark_mode,
                checked = value,
                onCheckedChange = {},
            )
        }
    }
}
