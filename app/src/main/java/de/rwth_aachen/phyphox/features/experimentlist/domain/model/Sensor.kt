package de.rwth_aachen.phyphox.features.experimentlist.domain.model


data class Sensor(
    val type: String,
    val rate: Double? = null,
    val output: List<SensorOutput> = emptyList(),
)
