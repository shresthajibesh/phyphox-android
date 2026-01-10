package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.domain.model.errors.AccessPortOutOfRange
import de.rwth_aachen.phyphox.features.settings.presentation.compose.seekbarpreferenceitem.SeekBarConfig
import de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem.SegmentedButtonUiModel
import de.rwth_aachen.phyphox.ui.string.ResourceStringUIModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.toStringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import java.util.Locale
import javax.inject.Inject

internal class UiBuilder @Inject constructor() {

    //region - App Language
    fun buildLanguageUiModel(localeResource: UiResourceState<Locale>): UiResourceState<StringUIModel> {
        return when (localeResource) {
            UiResourceState.Loading -> UiResourceState.Loading
            is UiResourceState.Success<Locale> -> UiResourceState.Success(
                mapLocaleToUiModel(localeResource.data),
            )
        }
    }

    private fun mapLocaleToUiModel(locale: Locale): StringUIModel {
        return locale.displayName.toStringUIModel()
    }
    //endregion

    //region - AppUiMode
    fun buildAppUiModeResource(
        current: UiResourceState<AppUiMode>,
        modes: UiResourceState<List<AppUiMode>>,
    ): UiResourceState<List<SegmentedButtonUiModel<AppUiMode>>> {
        return if (current is UiResourceState.Success && modes is UiResourceState.Success) {
            UiResourceState.Success(
                modes.data.map { mode ->
                    SegmentedButtonUiModel(
                        item = mode,
                        isSelected = current.data == mode,
                        text = getSupportedUiModeText(mode),
                    )
                },
            )
        } else {
            UiResourceState.Loading
        }
    }

    private fun getSupportedUiModeText(appUiMode: AppUiMode): StringUIModel {
        return when (appUiMode) {
            AppUiMode.SYSTEM -> ResourceStringUIModel(R.string.settings_mode_dark_system)
            AppUiMode.LIGHT -> ResourceStringUIModel(R.string.settings_mode_no_dark)
            AppUiMode.DARK -> ResourceStringUIModel(R.string.settings_mode_dark)
        }
    }

    //endregion

    //region - AccessPort
    fun buildAccessPortUiModel(
        portResource: UiResourceState<Int>,
    ): UiResourceState<StringUIModel> {
        return if (portResource is UiResourceState.Success) {
            UiResourceState.Success(
                portResource.data.toString().toStringUIModel(),
            )
        } else {
            UiResourceState.Loading
        }
    }
    //endregion

    //region - Graph Size
    fun buildGraphSizeUiModel(
        currentGraphSizeFlow: UiResourceState<Float>,
        graphSizeRangeFlow: UiResourceState<ClosedFloatingPointRange<Float>>,
    ): UiResourceState<SeekBarConfig> {
        return when {
            currentGraphSizeFlow is UiResourceState.Success && graphSizeRangeFlow is UiResourceState.Success -> {
                UiResourceState.Success(
                    SeekBarConfig(
                        currentSize = currentGraphSizeFlow.data,
                        range = graphSizeRangeFlow.data.start..graphSizeRangeFlow.data.endInclusive,
                    ),
                )
            }

            else -> UiResourceState.Loading
        }
    }

    fun getErrorMessage(error: Throwable): StringUIModel {
        return when (error) {
            is AccessPortOutOfRange -> "${error.intRange.first} - ${error.intRange.last}".toStringUIModel()
            else -> "Unknown error".toStringUIModel()
        }
    }
    //endregion
}
