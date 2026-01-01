package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val uiBuilder: UiBuilder,
) : ViewModel() {

    private val currentLanguageFlow = MutableStateFlow<ResourceState<StringUIModel>>(ResourceState.Loading)
    private val supportedLanguagesFlow = MutableStateFlow<ResourceState<List<StringUIModel>>>(ResourceState.Loading)
    private val currentGraphSizeFlow = MutableStateFlow<ResourceState<Float>>(ResourceState.Loading)
    private val graphSizeRangeFlow =
        MutableStateFlow<ResourceState<ClosedFloatingPointRange<Float>>>(ResourceState.Loading)
    private val currentUiModeUiModelFlow = MutableStateFlow<ResourceState<AppUiMode>>(ResourceState.Loading)
    private val supportedUiModesFlowUiModel = MutableStateFlow<ResourceState<List<AppUiMode>>>(ResourceState.Loading)
    private val currentAccessPortFlow = MutableStateFlow<ResourceState<StringUIModel>>(ResourceState.Loading)
    private val proximityLockEnabledFlow = MutableStateFlow<ResourceState<Boolean>>(ResourceState.Loading)

    private val graphSizeFlow = combine(currentGraphSizeFlow, graphSizeRangeFlow) { graphSize, range ->
        if (graphSize is ResourceState.Success && range is ResourceState.Success) {
            ResourceState.Success(
                data = uiBuilder.buildSeekBarConfig(
                    currentSize = graphSize.data,
                    range = range.data,
                ),
            )
        } else {
            ResourceState.Loading
        }
    }

    private val uiModeFlow = combine(currentUiModeUiModelFlow, supportedUiModesFlowUiModel) { current, modes ->
        if (current is ResourceState.Success && modes is ResourceState.Success) {
            ResourceState.Success(
                uiBuilder.buildUiModeUiModels(
                    currentUiModeUiMode = current.data,
                    supportedModes = modes.data,
                ),
            )
        } else {
            ResourceState.Loading
        }
    }

    val uiState = combine(
        currentLanguageFlow,
        graphSizeFlow,
        uiModeFlow,
        currentAccessPortFlow,
        proximityLockEnabledFlow,
    ) { currentLanguage, graphSize, uiMode, accessPort, proximityLockEnabled ->
        SettingsUiState(
            currentLanguage = currentLanguage,
            graphSize = graphSize,
            uiModeUiModel = uiMode,
            accessPort = accessPort,
            proximityLockEnabled = proximityLockEnabled,
        )
    }
}
