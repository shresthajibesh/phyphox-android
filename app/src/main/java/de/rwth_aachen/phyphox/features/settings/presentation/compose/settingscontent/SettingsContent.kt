package de.rwth_aachen.phyphox.features.settings.presentation.compose.settingscontent

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.presentation.compose.preferencecategoryheader.PreferenceCategoryHeader
import de.rwth_aachen.phyphox.features.settings.presentation.compose.preferenceitem.PreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarPreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.compose.switchpreferenceitem.SwitchPreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SeekBarConfig
import de.rwth_aachen.phyphox.ui.string.ResourceStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    currentLanguage: ResourceState<StringUIModel>,
    seekbarConfig: ResourceState<SeekBarConfig>,
    uiMode: ResourceState<StringUIModel>,
    accessPort: ResourceState<StringUIModel>,
    proximityLockEnabled: ResourceState<Boolean>,
    onAppLanguageClicked: () -> Unit,
    onLearnMoreAboutTranslationClicked: () -> Unit,
    onGraphSizeChanged: (Float) -> Unit,
    onUiModeClicked: () -> Unit,
    onAccessPortClicked: () -> Unit,
    onProximityLockChanged: (Boolean) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        //TODO: Once the experiment list has been migrated to compose
        // move the logo from top bar in experiment list to here
        item {
            PreferenceCategoryHeader(
                title = ResourceStringUIModel(resId = R.string.settingsHeadLanguage),
            )
        }
        item {
            PreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settingsLanguage),
                summary = currentLanguage,
                iconRes = R.drawable.setting_language,
                onClick = onAppLanguageClicked,
            )
        }
        item {
            PreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settingsTranslation),
                summary = ResourceState.Success(ResourceStringUIModel(R.string.settingsTranslationMore)),
                iconRes = R.drawable.setting_translate,
                onClick = onLearnMoreAboutTranslationClicked,
            )
        }

        item {
            HorizontalDivider()
        }
        // Graph View Category
        item {
            PreferenceCategoryHeader(
                title = ResourceStringUIModel(resId = R.string.settingGraphViewEdit),
            )
        }
        item {
            SeekBarPreferenceItem(
                title = ResourceStringUIModel(R.string.settingGraphSize),
                seekBarConfig = seekbarConfig,
                onValueChange = onGraphSizeChanged,
            )
        }
        item {
            PreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settings_theme_title),
                summary = uiMode,
                iconRes = R.drawable.ic_dark_mode,
                onClick = onUiModeClicked
            )
        }
        item {
            HorizontalDivider()
        }
        // Advanced Category
        item {
            PreferenceCategoryHeader(title = ResourceStringUIModel(resId = R.string.settingsHeadAdvanced))
        }
        item {
            //replace this with SingleChoiceSegmentedButtonRow
            PreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settingsPort),
                summary = accessPort,
                iconRes = R.drawable.setting_http,
                onClick = onAccessPortClicked,
            )
        }
        item {
            SwitchPreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settingsProximityLock),
                summary = ResourceStringUIModel(resId = R.string.settingsProximityLockDetail),
                iconRes = R.drawable.setting_lock,
                checked = proximityLockEnabled,
                onCheckedChange = onProximityLockChanged,
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
fun SettingsContentPreview() {
    PhyphoxTheme {
        Surface {
            SettingsContent(
                currentLanguage = ResourceState.Loading,
                seekbarConfig = ResourceState.Loading,
                uiMode = ResourceState.Loading,
                accessPort = ResourceState.Loading,
                proximityLockEnabled = ResourceState.Loading,
                onAppLanguageClicked = {},
                onLearnMoreAboutTranslationClicked = {},
                onGraphSizeChanged = {},
                onUiModeClicked = {},
                onAccessPortClicked = {},
                onProximityLockChanged = {},
            )
        }
    }
}
