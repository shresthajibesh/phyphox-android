package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport

import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.GetAccessPortRangeApplicationService
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.ObserveCurrentAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.UpdateAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiBuilder
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.toStringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultAccessPortDelegate @Inject constructor(
    private val observeCurrentAccessPort: ObserveCurrentAccessPortUseCase,
    private val getAccessPortRange: GetAccessPortRangeApplicationService,
    private val updateAccessPort: UpdateAccessPortUseCase,
    private val uiBuilder: UiBuilder,
) : AccessPortDelegate {
    private val currentAccessPortStateFlow = MutableStateFlow<UiResourceState<Int>>(UiResourceState.Loading)

    override val accessPortFlow: Flow<UiResourceState<StringUIModel>> = currentAccessPortStateFlow.map { accessPort ->
        uiBuilder.buildAccessPortUiModel(accessPort)
    }

    private val inputModalStaFlow = MutableStateFlow<AccessPortSheetUiModel?>(null)
    override val inputModal: Flow<AccessPortSheetUiModel?> = inputModalStaFlow

    override fun start(scope: CoroutineScope) {
        observeCurrentAccessPort().onStart {
            currentAccessPortStateFlow.value = UiResourceState.Loading
        }.onEach {
            currentAccessPortStateFlow.value = UiResourceState.Success(it)
        }.launchIn(scope)
    }

    override suspend fun showAccessPortInputModal() {
        val current = currentAccessPortStateFlow.value
        val range = getAccessPortRange()
        if (
            current is UiResourceState.Success
        ) {
            inputModalStaFlow.value = AccessPortSheetUiModel(
                currentPort = current.data.toString().toStringUIModel(),
                range = range,
            )
        }
    }

    override suspend fun setAccessPort(newPort: String) {
        updateAccessPort(newPort)
        inputModalStaFlow.value = null
    }

    override suspend fun dismissAccessPortInputModal() {
        inputModalStaFlow.value = null
    }
}

