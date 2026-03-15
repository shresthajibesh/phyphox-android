package de.rwth_aachen.phyphox.ExperimentList.domain.model

data class Translation(
    val locale: String,
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val localizedString: List<LocalizedString> = emptyList(),
)
