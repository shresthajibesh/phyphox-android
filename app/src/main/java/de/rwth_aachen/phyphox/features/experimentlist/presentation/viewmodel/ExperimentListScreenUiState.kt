package de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel

import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX

sealed interface ExperimentListScreenUiState {
    data object Loading : ExperimentListScreenUiState
    data class Error(val message: String) : ExperimentListScreenUiState
    data class Success(
        val experiments: Map<String, List<PhyphoxExperimentX>>,
        val displayType: DisplayType = DisplayType.List,
    ) : ExperimentListScreenUiState
}

enum class DisplayType(val nameId:Int) {
    List(R.string.display_type_sheet_list), Grouped(R.string.display_type_sheet_grouped_list)

}
