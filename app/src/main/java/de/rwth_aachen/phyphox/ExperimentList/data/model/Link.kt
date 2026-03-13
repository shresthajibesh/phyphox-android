package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class Link(

    val label: String? = null,

    @XmlValue
    val url: String,
)
