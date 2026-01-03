package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode

import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode.GetSupportedAppUiModeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode.ObserveCurrentAppUiModeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode.UpdateCurrentAppUiModeUseCase
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DefaultAppUiModeDelegate @Inject constructor(
    private val observeCurrentUiMode: ObserveCurrentAppUiModeUseCase,
    private val getSupportedUiModes: GetSupportedAppUiModeUseCase,
    private val updateCurrentAppUiMode: UpdateCurrentAppUiModeUseCase
) : AppUiModeDelegate {
    private val currentUiModeUiModelFlow = MutableStateFlow<UiResourceState<AppUiMode>>(UiResourceState.Loading)
    private val supportedUiModesFlowUiModel =
        MutableStateFlow<UiResourceState<List<AppUiMode>>>(UiResourceState.Loading)

    override val appUiModeFlow: Flow<UiResourceState<List<AppUiModeState>>> =
        combine(currentUiModeUiModelFlow, supportedUiModesFlowUiModel) { current, modes ->
            if (current is UiResourceState.Success && modes is UiResourceState.Success) {
                UiResourceState.Success(
                    modes.data.map { mode ->
                        AppUiModeState(
                            appUiMode = mode,
                            isSelected = current.data == mode,
                        )
                    },
                )
            } else {
                UiResourceState.Loading
            }
        }
}

data class AppUiModeState(
    val appUiMode: AppUiMode,
    val isSelected: Boolean,
)
