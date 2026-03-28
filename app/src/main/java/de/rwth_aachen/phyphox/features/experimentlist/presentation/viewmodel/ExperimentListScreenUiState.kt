package de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel

import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX

sealed interface ExperimentListScreenUiState {
    data object Loading : ExperimentListScreenUiState
    data class Error(val message: String) : ExperimentListScreenUiState
    data class Success(val experiments: List<PhyphoxExperimentX>) : ExperimentListScreenUiState
}
