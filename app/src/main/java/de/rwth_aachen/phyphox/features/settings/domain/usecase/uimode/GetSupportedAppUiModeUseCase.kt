package de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import kotlinx.coroutines.delay
import javax.inject.Inject

class GetSupportedAppUiModeUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    suspend operator fun invoke(): List<AppUiMode> {
        delay(2500)
        return AppUiMode.entries
    }
}
