package de.rwth_aachen.phyphox.features.settings.presentation.composeable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SettingsUiState
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.string.toStringUIModel
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoot(
    modifier: Modifier = Modifier,
    uiState: SettingsUiState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.action_settings)) }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        SettingsContent(
            modifier = modifier.padding(innerPadding),
        )
    }
}

@Composable
@PreviewLightDark
internal fun SettingsRootPreview() {
    PhyphoxTheme {
        SettingsRoot(uiState = SettingsUiState())
    }
}



@Composable
fun ListHeaderItem(
    modifier: Modifier = Modifier,
    text: StringUIModel,
) {
    Text(
        modifier = modifier,
        text = text.resolve(),
    )
}

@PreviewLightDark
@Composable
private fun ListHeaderItemPreview() {
    PhyphoxTheme {
        ListHeaderItem(text = LoremIpsum(2).values.first().toStringUIModel())
    }
}
