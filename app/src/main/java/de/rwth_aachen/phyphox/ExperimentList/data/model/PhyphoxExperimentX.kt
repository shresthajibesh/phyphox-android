package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("phyphox", "", "")
data class PhyphoxExperimentX(
    @XmlOtherAttributes val version: String,
    @XmlOtherAttributes val locale: String? = null,
    @XmlElement val title: String? = null,
    @XmlElement val category: String? = null,
    @XmlSerialName("icon", "", "") val icon: Icon? = null,
    @XmlSerialName("link", "", "") val link: Link? = null,
    @XmlSerialName("translations", "", "") val translations: Translations? = null,
    @XmlSerialName("data-containers", "", "") val dataContainers: DataContainers? = null,
    @XmlSerialName("input", "", "") val input: Input? = null,
    )
