package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport

import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.GetAccessPortRangeApplicationService
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.ObserveCurrentAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.UpdateAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.SettingsSheetUiModel
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiBuilder
import de.rwth_aachen.phyphox.ui.string.toStringUIModel
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

internal class DefaultAccessPortDelegate @Inject constructor(
    private val observeCurrentAccessPort: ObserveCurrentAccessPortUseCase,
    private val getAccessPortRange: GetAccessPortRangeApplicationService,
    private val updateAccessPort: UpdateAccessPortUseCase,
    private val uiBuilder: UiBuilder,
) : AccessPortDelegate {
    private val currentAccessPortStateFlow = MutableStateFlow<UiResourceState<Int>>(UiResourceState.Loading)
    private val accessPortRangeStateFlow = MutableStateFlow<UiResourceState<IntRange>>(UiResourceState.Loading)
    private val inputModalStaFlow = MutableStateFlow<SettingsSheetUiModel.AccessPortSheetUiModel?>(null)

    override val accessPortFlow: Flow<UiResourceState<AccessPortUiState>> = combine(
        currentAccessPortStateFlow,
        accessPortRangeStateFlow,
        inputModalStaFlow,
    ) { accessPort, range, inputModal ->
        uiBuilder.buildAccessPortUiModel(accessPort, range, inputModal)
    }

    override fun start(scope: CoroutineScope) {
        observeCurrentAccessPort().onStart {
            currentAccessPortStateFlow.value = UiResourceState.Loading
        }.onEach {
            currentAccessPortStateFlow.value = UiResourceState.Success(it)
        }.launchIn(scope)
        scope.launch {
            getAccessPortRange().let {
                accessPortRangeStateFlow.value = UiResourceState.Success(it)
            }
        }
    }

    override suspend fun showAccessPortInputModal() {
        val current = currentAccessPortStateFlow.value
        val range = accessPortRangeStateFlow.value
        if (
            current is UiResourceState.Success &&
            range is UiResourceState.Success
        ) {
            inputModalStaFlow.value = SettingsSheetUiModel.AccessPortSheetUiModel(
                currentPort = current.data.toString().toStringUIModel(),
                range = range.data,
            )
        }
    }

    override suspend fun setAccessPort(newPort: String) {
        updateAccessPort(newPort)
    }

    override suspend fun dismissAccessPortInputModal() {
        inputModalStaFlow.value = null
    }
}

