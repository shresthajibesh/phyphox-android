package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
data class Translations(
    @XmlSerialName("translation", "", "") val translation: List<Translation> = emptyList(),
)
