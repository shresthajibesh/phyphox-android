package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode

import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode.GetSupportedAppUiModeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode.ObserveCurrentAppUiModeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode.UpdateCurrentAppUiModeUseCase
import de.rwth_aachen.phyphox.features.settings.presentation.compose.segmentedbuttonpreferenceitem.SegmentedButtonUiModel
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiBuilder
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class DefaultAppUiModeDelegate @Inject constructor(
    private val observeCurrentUiMode: ObserveCurrentAppUiModeUseCase,
    private val getSupportedUiModes: GetSupportedAppUiModeUseCase,
    private val updateCurrentAppUiMode: UpdateCurrentAppUiModeUseCase,
    private val uiBuilder: UiBuilder,
) : AppUiModeDelegate {
    private val currentUiModeUiModelFlow = MutableStateFlow<UiResourceState<AppUiMode>>(UiResourceState.Loading)
    private val supportedUiModesFlowUiModel =
        MutableStateFlow<UiResourceState<List<AppUiMode>>>(UiResourceState.Loading)

    override val appUiModeFlow: Flow<UiResourceState<List<SegmentedButtonUiModel<AppUiMode>>>> =
        combine(currentUiModeUiModelFlow, supportedUiModesFlowUiModel) { current, modes ->
            uiBuilder.buildAppUiModeResource(current, modes)
        }

    override fun start(scope: CoroutineScope) {
        observeAppUiMode(scope)
        fetchSupportedUiModes(scope)
    }

    override suspend fun updateAppUiMode(appUiMode: SegmentedButtonUiModel<AppUiMode>) {

    }

    private fun fetchSupportedUiModes(scope: CoroutineScope) {
        scope.launch {
            supportedUiModesFlowUiModel.value = UiResourceState.Success(
                getSupportedUiModes()
            )
        }
    }

    private fun observeAppUiMode(scope: CoroutineScope){
        observeCurrentUiMode().onStart {
            currentUiModeUiModelFlow.value = UiResourceState.Loading
        }.onEach {
            currentUiModeUiModelFlow.value = UiResourceState.Success(it)
        }.launchIn(scope)
    }
}

