package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("translation", "", "")
data class Translation(
    @XmlSerialName("locale", "", "") // Sets attribute name
    @XmlElement(false)
    val locale: String,

    val title: String? = null,
    val category: String? = null,

    @XmlElement(true)
    val description: String? = null,

    val string: List<LocalizedString> = emptyList()
)
