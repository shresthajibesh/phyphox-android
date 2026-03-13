package de.rwth_aachen.phyphox.ExperimentList.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlOtherAttributes
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("phyphox", "", "")
data class PhyphoxExperimentX(
    @XmlOtherAttributes val version: String,
    @XmlOtherAttributes val locale: String? = null,

    @XmlSerialName("icon", "", "") val icon: Icon? = null,
    @XmlSerialName("link", "", "") val link: Link? = null,

    //    val category: String,
//    val description: String,
//
//    val translations: Translations? = null,
//
//    val input: InputSection? = null,
//
//    @XmlSerialName("data-containers", "", "")
//    val dataContainers: DataContainers? = null,
//
//    val analysis: Analysis? = null,
//
//    val views: Views? = null
)


@Serializable
data class Icon(
    val format: String? = null,

    @XmlValue
    val value: String
)


@Serializable
data class Link(

    val label: String? = null,

    @XmlValue
    val url: String
)
