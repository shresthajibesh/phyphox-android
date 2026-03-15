package de.rwth_aachen.phyphox.features.experimentlist.domain.model


data class PhyphoxExperimentX(
    val version: String,
    val locale: String? = null,
    val title: String? = null,
    val category: String? = null,
    val icon: Icon? = null,
    val link: Link? = null,
    val translations: Translations? = null,
    val dataContainers: DataContainers? = null,
    val input: Input? = null,
)
