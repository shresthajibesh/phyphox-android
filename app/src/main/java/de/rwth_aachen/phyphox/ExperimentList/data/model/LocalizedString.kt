package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("string", "", "")
data class LocalizedString(


    @XmlSerialName("original", "", "") // Sets attribute name
    @XmlElement(false)
    val original: String,

    @XmlElement(true)
    val value: String,
)
