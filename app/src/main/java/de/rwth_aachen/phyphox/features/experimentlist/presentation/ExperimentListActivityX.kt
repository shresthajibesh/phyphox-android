package de.rwth_aachen.phyphox.features.experimentlist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    Scaffold(
        topBar = {
        },
        bottomBar = {
            MainBottomAppBar()
        }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        when (uiState) {
            ExperimentListScreenUiState.Loading -> ExperimentListLoadingContent(modifier)
            is ExperimentListScreenUiState.Error -> ExperimentListErrorContent(modifier, uiState.message)
            is ExperimentListScreenUiState.Success -> ExperimentListSuccessContent(modifier, uiState.experiments)
        }
    }
}

@Composable
fun MainBottomAppBar(modifier: Modifier = Modifier) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(Icons.Filled.Check, contentDescription = "Localized description")
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.AccountBox,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Localized description",
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* do something */ },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Filled.Add, "Localized description")
            }
        }
    )
}
