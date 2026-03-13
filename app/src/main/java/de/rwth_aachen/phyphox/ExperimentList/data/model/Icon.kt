package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Icon(
    val format: String? = null,

    @XmlValue
    val value: String,
)
