package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("icon", "", "")
data class Icon(

    @XmlSerialName("format", "", "") // Sets attribute name
    @XmlElement(false)
    val format: String,

    @XmlElement(true)
    val value: String
)
