package de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.features.experimentlist.domain.LoadExperimentsFromAssetsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperimentListViewModel @Inject constructor(
    private val loadExperimentsFromAssets: LoadExperimentsFromAssetsUseCase
) : ViewModel() {

    init {
        loadData()
    }

    fun ld() {}

    private fun loadData() = viewModelScope.launch {
        loadExperimentsFromAssets
    }
}
