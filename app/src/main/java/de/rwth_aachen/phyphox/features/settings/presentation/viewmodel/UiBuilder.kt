package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.ui.string.ResourceStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import javax.inject.Inject

internal class UiBuilder @Inject constructor() {
    fun buildSeekBarConfig(
        currentSize: Float,
        range: ClosedFloatingPointRange<Float>,
    ): SeekBarConfig {
        return SeekBarConfig(
            currentSize = currentSize,
            minSize = range.start,
            maxSize = range.endInclusive,
        )
    }

    fun buildUiModeUiModels(
        currentUiModeUiMode: AppUiMode,
        supportedModes: List<AppUiMode>,
    ): List<UiModeUiModel> {
        return supportedModes.map { mode ->
            UiModeUiModel(
                appUiMode = mode,
                text = getSupportedUiModeText(mode),
                isSelected = mode == currentUiModeUiMode,
            )
        }
    }

    private fun getSupportedUiModeText(appUiMode: AppUiMode): StringUIModel {
        return when (appUiMode) {
            AppUiMode.SYSTEM -> ResourceStringUIModel(R.string.settings_mode_dark_system)
            AppUiMode.LIGHT -> ResourceStringUIModel(R.string.settings_mode_no_dark)
            AppUiMode.DARK -> ResourceStringUIModel(R.string.settings_mode_dark)
        }
    }
}
