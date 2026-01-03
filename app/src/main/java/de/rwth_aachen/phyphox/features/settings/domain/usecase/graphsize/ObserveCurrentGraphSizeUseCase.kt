package de.rwth_aachen.phyphox.features.settings.domain.usecase.graphsize

import de.rwth_aachen.phyphox.features.settings.domain.model.GraphConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveCurrentGraphSizeUseCase @Inject constructor(

) {
    operator fun invoke(): Flow<GraphConfig> {
        return flowOf(
            GraphConfig(
                labelSizeMultiplier = 1.0f,
                textSizeMultiplier = 1.0f,
                lineWidthMultiplier = 1.0f,
                borderWidthMultiplier = 1.0f,
            ),
        )
    }
}
