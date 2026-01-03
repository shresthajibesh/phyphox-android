package de.rwth_aachen.phyphox.features.settings.domain.usecase.language

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import javax.inject.Inject

class UpdateAppLanguageUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
){
}
