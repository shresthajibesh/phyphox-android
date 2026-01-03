package de.rwth_aachen.phyphox.features.settings.domain.usecase.language

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale
import javax.inject.Inject

class ObserveCurrentAppLanguageUseCase @Inject constructor() {
    operator fun invoke(): Flow<Locale> {
        return flowOf(Locale.ENGLISH)
    }
}
