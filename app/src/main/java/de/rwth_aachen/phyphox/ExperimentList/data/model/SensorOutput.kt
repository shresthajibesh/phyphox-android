package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
data class SensorOutput(
    @XmlOtherAttributes val component: String,
    @XmlValue val name: String,
)
