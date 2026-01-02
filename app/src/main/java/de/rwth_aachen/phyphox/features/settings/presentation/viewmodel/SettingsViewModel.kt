package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.TextStringUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    init {
        loadDummyData()
    }

    private fun loadDummyData() = viewModelScope.launch {
        delay(800)
        currentLanguageFlow.value = ResourceState.Success(TextStringUIModel("English"))
        delay(1200)
        currentGraphSizeFlow.value = ResourceState.Success(
            data = 1.0f,
        )
        delay(200)
        graphSizeRangeFlow.value = ResourceState.Success(
            data = 0.0f..3.0f,
        )
        delay(800)
        currentUiModeUiModelFlow.value = ResourceState.Success(
            data = AppUiMode.DARK,
        )
        supportedUiModesFlowUiModel.value = ResourceState.Success(
            AppUiMode.entries,
        )
        delay(600)
        currentAccessPortFlow.value = ResourceState.Success(
            data = TextStringUIModel("8080"),
        )
        delay(2000)
        proximityLockEnabledFlow.value = ResourceState.Success(
            data = true,
        )
    }

    fun onActionEvent(action: SettingsAction){

    }
}
