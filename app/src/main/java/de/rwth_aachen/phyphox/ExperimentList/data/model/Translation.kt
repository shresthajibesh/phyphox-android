package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Translation(

    val locale: String,

    @XmlElement val title: String? = null,
    @XmlElement val category: String? = null,
    @XmlElement val description: String? = null,
    @XmlSerialName("string", "", "")val localizedString: List<LocalizedString> = emptyList()
)
