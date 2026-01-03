package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport

import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.GetAccessPortRangeUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.ObserveCurrentAccessPortUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport.UpdateAccessPortUseCase
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DefaultAccessPortDelegate @Inject constructor(
    private val observeCurrentAccessPort: ObserveCurrentAccessPortUseCase,
    private val getAccessPortRange: GetAccessPortRangeUseCase,
    private val updateAccessPort: UpdateAccessPortUseCase
) : AccessPortDelegate {
    private val currentAccessPortStateFlow = MutableStateFlow<UiResourceState<Int>>(UiResourceState.Loading)
    override val accessPortFlow: Flow<UiResourceState<Int>> = currentAccessPortStateFlow

}

