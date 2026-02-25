package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("link", "", "")
data class Link(
    @XmlSerialName("label", "", "") // Sets attribute name
    @XmlElement(false)
    val label: String,

    @XmlElement(true)
    val url: String
)
