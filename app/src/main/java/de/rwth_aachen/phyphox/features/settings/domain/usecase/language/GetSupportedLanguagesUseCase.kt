package de.rwth_aachen.phyphox.features.settings.domain.usecase.language

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import javax.inject.Inject

class GetSupportedLanguagesUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
){
}
