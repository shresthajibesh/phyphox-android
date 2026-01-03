package de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveCurrentAppUiModeUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    operator fun invoke(): Flow<AppUiMode> {
        return flowOf(AppUiMode.LIGHT)
    }
}
