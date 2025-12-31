package de.rwth_aachen.phyphox.features.settings.presentation.composeable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {
        // Language Category
        item {
            PreferenceCategory(title = stringResource(id = R.string.settingsHeadLanguage))
        }
        item {
            PreferenceItem(
                title = stringResource(id = R.string.settingsLanguage),
                summary = "English", // Mock summary
                iconRes = R.drawable.setting_language,
            )
        }
        item {
            PreferenceItem(
                title = stringResource(id = R.string.settingsTranslation),
                summary = stringResource(id = R.string.settingsTranslationMore),
                iconRes = R.drawable.setting_translate,
            )
        }

        // Graph View Category
        item {
            PreferenceCategory(title = stringResource(id = R.string.settingGraphViewEdit))
        }
        item {
            var graphSize by remember { mutableFloatStateOf(1f) }
            SeekBarPreferenceItem(
                title = "Graph size", // Mock title
                value = graphSize,
                valueRange = 0f..3f,
                onValueChange = { graphSize = it },
            )
        }
        item {
            PreferenceItem(
                title = stringResource(id = R.string.settings_theme_title),
                summary = stringResource(id = R.string.settings_mode_dark_system), // Mock summary
                iconRes = R.drawable.ic_dark_mode,
            )
        }

        // Advanced Category
        item {
            PreferenceCategory(title = stringResource(id = R.string.settingsHeadAdvanced))
        }
        item {
            PreferenceItem(
                title = stringResource(id = R.string.settingsPort),
                summary = "8080",
                iconRes = R.drawable.setting_http,
            )
        }
        item {
            var proximityLock by remember { mutableStateOf(false) }
            SwitchPreferenceItem(
                title = stringResource(id = R.string.settingsProximityLock),
                summary = stringResource(id = R.string.settingsProximityLockDetail),
                iconRes = R.drawable.setting_lock,
                checked = proximityLock,
                onCheckedChange = { proximityLock = it },
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun SettingsContentPreview() {
    PhyphoxTheme {
        SettingsContent()
    }
}
