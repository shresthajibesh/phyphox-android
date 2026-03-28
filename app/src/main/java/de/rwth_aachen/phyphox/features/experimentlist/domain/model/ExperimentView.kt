package de.rwth_aachen.phyphox.features.experimentlist.domain.model

data class ExperimentView(
    val label: String?,
    val elements: List<ViewElement>,
)

sealed interface ViewElement

data class GraphElement(
    val label: String?,
    val logX: Boolean,
    val logY: Boolean,
    val logZ: Boolean,
    val labelX: String?,
    val unitX: String?,
    val labelY: String?,
    val unitY: String?,
    val labelZ: String?,
    val unitZ: String?,
    val aspectRatio: String?,
    val style: String?,
    val mapWidth: String?,
    val partialUpdate: Boolean?,
    val inputs: List<String>,
) : ViewElement

data class ValueElement(
    val label: String?,
    val inputs: List<String>,
) : ViewElement

data class SeparatorElement(
    val height: Int?,
) : ViewElement

data class InfoElement(
    val label: String?,
) : ViewElement
