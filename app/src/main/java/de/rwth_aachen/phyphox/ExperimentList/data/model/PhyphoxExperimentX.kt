package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import javax.xml.namespace.QName

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


@Serializable
data class Input(
    @XmlSerialName("sensor", "", "") val sensors: List<Sensor> = emptyList()
)

@Serializable
data class Sensor(
    val type: String,
    val rate: Double? = null,

    @XmlSerialName("output", "", "") val output: List<SensorOutput> = emptyList()
)

@Serializable
data class SensorOutput(
    @XmlOtherAttributes val component: String,
    @XmlValue val name: String,
)

