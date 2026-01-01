package de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.presentation.compose.preferenceitem.PreferenceItem
import de.rwth_aachen.phyphox.features.settings.presentation.compose.preferencesummaryitem.PreferenceSummaryItem
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiBuilder
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiModeUiModel
import de.rwth_aachen.phyphox.ui.skeleton
import de.rwth_aachen.phyphox.ui.string.LoremIpsumStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@Composable
fun SegmentedButtonPreferenceItem(
    modifier: Modifier = Modifier,
    title: StringUIModel,
    summary: StringUIModel? = null,
    iconRes: Int? = null,
    options: ResourceState<List<UiModeUiModel>>,
    onOptionSelected: (UiModeUiModel) -> Unit,
) {
    PreferenceItem(
        modifier = modifier,
        title = title,
        iconRes = iconRes,
        content = {
            summary?.let {
                PreferenceSummaryItem(text = summary)
            }
            when (options) {
                ResourceState.Loading -> Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .fillMaxWidth()
                        .height(16.dp)
                        .skeleton(),
                )

                is ResourceState.Success<List<UiModeUiModel>> -> SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    options.data.forEachIndexed { index, option ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.data.size,
                            ),
                            onClick = {
                                onOptionSelected(option)
                            },
                            selected = option.isSelected,
                            label = { Text(option.text.resolve()) },
                        )
                    }
                }
            }

        },
    )
}

@PreviewLightDark
@Composable
internal fun SegmentedButtonPreferenceItemPreview() {
    PhyphoxTheme {
        SegmentedButtonPreferenceItem(
            title = LoremIpsumStringUIModel(4),
            summary = LoremIpsumStringUIModel(12),
            iconRes = R.drawable.ic_dark_mode,
            options = ResourceState.Success(
                listOf(
                    UiModeUiModel(
                        appUiMode = AppUiMode.DARK,
                        text = LoremIpsumStringUIModel(2),
                        isSelected = true,
                    ),
                    UiModeUiModel(
                        appUiMode = AppUiMode.SYSTEM,
                        text = LoremIpsumStringUIModel(1),
                        isSelected = false,
                    ),
                    UiModeUiModel(
                        appUiMode = AppUiMode.LIGHT,
                        text = LoremIpsumStringUIModel(1),
                        isSelected = false,
                    ),
                ),
            ),
            onOptionSelected = {},
        )
    }
}
