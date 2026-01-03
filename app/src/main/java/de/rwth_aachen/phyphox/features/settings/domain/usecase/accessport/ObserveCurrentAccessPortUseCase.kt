package de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveCurrentAccessPortUseCase @Inject constructor() {
    operator fun invoke(): Flow<Int> {
        return flowOf(8080)
    }
}
