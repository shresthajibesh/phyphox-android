package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates

import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import kotlinx.coroutines.flow.StateFlow

interface ProximityLockDelegate {
    val proximityLockEnabledStateFlow: StateFlow<ResourceState<Boolean>>
}
