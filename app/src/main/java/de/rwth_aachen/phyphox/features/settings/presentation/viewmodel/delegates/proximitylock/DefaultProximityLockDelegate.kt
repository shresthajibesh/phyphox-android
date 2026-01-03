package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.proximitylock

import de.rwth_aachen.phyphox.features.settings.domain.usecase.proximitylock.ObserveIsCurrentProximityLockEnabledUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.proximitylock.UpdateProximityLockStatusUseCase
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DefaultProximityLockDelegate @Inject constructor(
    private val observeProximityLockEnabled: ObserveIsCurrentProximityLockEnabledUseCase,
    private val updateProximityLockEnabled: UpdateProximityLockStatusUseCase,
) : ProximityLockDelegate {

    private val proximityLockEnabledFlow = MutableStateFlow<UiResourceState<Boolean>>(UiResourceState.Loading)

    override val proximityLockFlow: Flow<UiResourceState<Boolean>> = proximityLockEnabledFlow
}
