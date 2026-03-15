package de.rwth_aachen.phyphox.features.experimentlist.domain.model

data class Translation(
    val locale: String? = null,
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val localizedString: List<LocalizedString> = emptyList(),
)
