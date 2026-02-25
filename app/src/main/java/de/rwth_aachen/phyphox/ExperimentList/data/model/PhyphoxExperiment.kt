package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("phyphox", "", "")
data class PhyphoxExperiment(

    @XmlSerialName("version", "", "") // Sets attribute name
    @XmlElement(false)
    val version: String,

    @XmlSerialName("locale", "", "") // Sets attribute name
    @XmlElement(false)
    val locale: String,

    val title: String,
    val category: String,

    val icon: Icon,

    @XmlElement(true)
    val description: String,

    val link: Link,

    val translations: Translations
)
