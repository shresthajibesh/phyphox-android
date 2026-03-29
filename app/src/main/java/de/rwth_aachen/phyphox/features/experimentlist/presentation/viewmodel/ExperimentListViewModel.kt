package de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.features.experimentlist.domain.LoadExperimentsFromAssetsUseCase
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperimentListViewModel @Inject constructor(
    private val loadExperimentsFromAssets: LoadExperimentsFromAssetsUseCase,
) : ViewModel() {

    private val _experimentList = MutableStateFlow<List<PhyphoxExperimentX>>(emptyList())
    private val _displayType = MutableStateFlow<DisplayType>(DisplayType.List)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState = combine(
        _experimentList,
        _displayType,
        _errorMessage,
        _isLoading,
    ) { experimentList, displayType, errorMessage, isLoading ->
        if (isLoading) {
            ExperimentListScreenUiState.Loading
        } else if (errorMessage != null) {
            ExperimentListScreenUiState.Error(errorMessage)
        } else {
            ExperimentListScreenUiState.Success(
                experiments = experimentList,
                displayType = displayType,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        ExperimentListScreenUiState.Loading,
    )


    init {
        loadExperimentsData()
    }

    private fun loadExperimentsData() = viewModelScope.launch {
        _isLoading.value = true
        loadExperimentsFromAssets().onSuccess { result ->
            _experimentList.value = result
            _isLoading.value = false
        }.onFailure {
            _errorMessage.value = it.message ?: "Unknown Error"
            _isLoading.value = false
        }
    }
}
