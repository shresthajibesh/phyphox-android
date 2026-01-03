package de.rwth_aachen.phyphox.features.settings.domain.usecase.graphsize

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@Deprecated("Migrating to multiplier based graph config instead.")
class ObserveCurrentGraphSizeUseCase @Inject constructor(

) {
    operator fun invoke(): Flow<Float> {
        return flowOf(0f)
    }
}
