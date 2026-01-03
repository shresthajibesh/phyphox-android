package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.proximitylock

import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.flow.Flow

interface ProximityLockDelegate {
    val proximityLockFlow: Flow<UiResourceState<Boolean>>
}
