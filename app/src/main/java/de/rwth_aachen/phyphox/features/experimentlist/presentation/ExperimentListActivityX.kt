package de.rwth_aachen.phyphox.features.experimentlist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.ExperimentListErrorContent
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.ExperimentListLoadingContent
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.ExperimentListSuccessContent
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.ExperimentListScreenUiState
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.ExperimentListViewModel
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme
import de.rwth_aachen.phyphox.utils.isDarkThemeEnabled

@AndroidEntryPoint
class ExperimentListActivityX : ComponentActivity() {

    private val viewModel: ExperimentListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhyphoxTheme(darkTheme = isDarkThemeEnabled()) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ExperimentListActivityScreen(uiState)
            }
        }
    }
}

@Composable
fun ExperimentListActivityScreen(uiState: ExperimentListScreenUiState) {
    Scaffold { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        when (uiState) {
            ExperimentListScreenUiState.Loading -> ExperimentListLoadingContent(modifier)
            is ExperimentListScreenUiState.Error -> ExperimentListErrorContent(modifier, uiState.message)
            is ExperimentListScreenUiState.Success -> ExperimentListSuccessContent(modifier, uiState.experiments)
        }
    }
}
