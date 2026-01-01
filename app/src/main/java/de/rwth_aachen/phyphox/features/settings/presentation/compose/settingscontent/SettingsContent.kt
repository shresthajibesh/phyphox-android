package de.rwth_aachen.phyphox.features.settings.presentation.compose.settingscontent

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.presentation.compose.clickablepreferenceitem.ClickablePreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.compose.preferencecategoryheader.PreferenceCategoryHeader
import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarPreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem.SegmentedButtonPreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.compose.switchpreferenceitem.SwitchPreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SeekBarConfig
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiModeUiModel
import de.rwth_aachen.phyphox.ui.string.ResourceStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    currentLanguage: ResourceState<StringUIModel>,
    seekbarConfig: ResourceState<SeekBarConfig>,
    uiModeUiModel: ResourceState<List<UiModeUiModel>>,
    accessPort: ResourceState<StringUIModel>,
    proximityLockEnabled: ResourceState<Boolean>,
    onAppLanguageClicked: () -> Unit,
    onLearnMoreAboutTranslationClicked: () -> Unit,
    onGraphSizeChanged: (Float) -> Unit,
    onOptionSelected: (UiModeUiModel) -> Unit,
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
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier
                        .width(172.dp),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(R.drawable.settings_logo),
                    contentDescription = null,
                )
            }
        }
        item { HorizontalDivider() }
        item {
            PreferenceCategoryHeader(
                title = ResourceStringUIModel(resId = R.string.settingsHeadLanguage),
            )
        }
        item {
            ClickablePreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settingsLanguage),
                summary = currentLanguage,
                iconRes = R.drawable.setting_language,
                onClick = onAppLanguageClicked,
            )
        }
        item {
            ClickablePreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settingsTranslation),
                summary = ResourceState.Success(ResourceStringUIModel(R.string.settingsTranslationMore)),
                iconRes = R.drawable.setting_translate,
                onClick = onLearnMoreAboutTranslationClicked,
            )
        }

        item { HorizontalDivider() }
        // Graph View Category
        item {
            PreferenceCategoryHeader(
                title = ResourceStringUIModel(resId = R.string.settingGraphViewEdit),
            )
        }
        item {
            SeekBarPreferenceItem(
                title = ResourceStringUIModel(R.string.settingGraphSize),
                summary = ResourceStringUIModel(R.string.settingGraphSizeSubTitle),
                iconRes = R.drawable.ic_line_width,
                seekBarConfig = seekbarConfig,
                onValueChange = onGraphSizeChanged,
            )
        }
        item {
            SegmentedButtonPreferenceItem(
                title = ResourceStringUIModel(resId = R.string.settings_theme_title),
                options = uiModeUiModel,
                iconRes = R.drawable.ic_dark_mode,
                onOptionSelected = onOptionSelected,
            )
        }
        item {
            HorizontalDivider()
        }
        // Advanced Category
        item {
            PreferenceCategoryHeader(
                title = ResourceStringUIModel(resId = R.string.settingsHeadAdvanced),
            )
        }
        item {
            ClickablePreferenceItem(
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
                uiModeUiModel = ResourceState.Loading,
                accessPort = ResourceState.Loading,
                proximityLockEnabled = ResourceState.Loading,
                onAppLanguageClicked = {},
                onLearnMoreAboutTranslationClicked = {},
                onGraphSizeChanged = {},
                onOptionSelected = {},
                onAccessPortClicked = {},
                onProximityLockChanged = {},
            )
        }
    }
}
