package de.rwth_aachen.phyphox.features.experimentlist.domain.model


data class PhyphoxExperimentX(
    val version: String,
    val locale: String? = null,
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val icon: Icon? = null,
    val links: List<Link> = emptyList(),
    val translations: List<Translation> = emptyList(),
    val dataContainers: List<Container> = emptyList(),
    val experimentInputs: List<ExperimentInput> = emptyList(),
//    val views = mutableListOf<View>()
//    val exports = mutableListOf<ExportSet>()
)
