package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.proximitylock

import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.SettingsDelegate
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ProximityLockDelegate: SettingsDelegate {
    val proximityLockFlow: Flow<UiResourceState<Boolean>>

    suspend fun updateProximityLockStatus(enabled: Boolean)
}
