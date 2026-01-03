package de.rwth_aachen.phyphox.features.settings.domain.usecase.uimode

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import javax.inject.Inject

class UpdateCurrentAppUiModeUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
){
}
