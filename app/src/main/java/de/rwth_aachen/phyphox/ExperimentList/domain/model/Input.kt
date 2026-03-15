package de.rwth_aachen.phyphox.ExperimentList.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Input(
    val sensors: List<Sensor> = emptyList(),
)
