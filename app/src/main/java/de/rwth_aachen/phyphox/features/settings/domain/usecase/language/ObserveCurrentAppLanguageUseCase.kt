package de.rwth_aachen.phyphox.features.settings.domain.usecase.language

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale
import javax.inject.Inject

class ObserveCurrentAppLanguageUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
) {
    operator fun invoke(): Flow<Locale> {
        return flowOf(Locale.ENGLISH)
    }
}
