package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport

import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.GetAccessPortRangeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.ObserveCurrentAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.UpdateAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiBuilder
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class DefaultAccessPortDelegate @Inject constructor(
    private val observeCurrentAccessPort: ObserveCurrentAccessPortUseCase,
    private val getAccessPortRange: GetAccessPortRangeUseCase,
    private val updateAccessPort: UpdateAccessPortUseCase,
    private val uiBuilder: UiBuilder,
) : AccessPortDelegate {
    private val currentAccessPortStateFlow = MutableStateFlow<UiResourceState<Int>>(UiResourceState.Loading)
    override val accessPortFlow: Flow<UiResourceState<StringUIModel>> = currentAccessPortStateFlow.map { portResource ->
        uiBuilder.buildAccessPortUiModel(portResource)
    }

    override fun start(scope: CoroutineScope) {
        observeCurrentAccessPort().onStart {
            currentAccessPortStateFlow.value = UiResourceState.Loading
        }.onEach {
            currentAccessPortStateFlow.value = UiResourceState.Success(it)
        }.launchIn(scope)
    }

    override suspend fun showAccessPortInputModal() {

    }

}

