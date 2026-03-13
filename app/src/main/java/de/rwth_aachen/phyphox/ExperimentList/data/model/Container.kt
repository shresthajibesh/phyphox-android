package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Container(
    val size: Double? = null,
    val init: Double? = null,

    @XmlValue
    val name: String,
)
