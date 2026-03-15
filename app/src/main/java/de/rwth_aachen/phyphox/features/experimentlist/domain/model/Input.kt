package de.rwth_aachen.phyphox.features.experimentlist.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Input(
    val sensors: List<Sensor> = emptyList(),
)
