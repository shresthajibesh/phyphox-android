package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.appbar.MainTopAppBar
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.sheets.NewExperimentBottomSheet
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.ExperimentListScreenUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentListActivityScreen(
    uiState: ExperimentListScreenUiState,
    onMoreClicked: () -> Unit,
    onItemClicked: (PhyphoxExperimentX) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val newExperimentSheetState = rememberModalBottomSheetState()
    var showNewExperimentBottomSheet by remember { mutableStateOf(false) }
    val isNewActionFabVisible = uiState is ExperimentListScreenUiState.Success

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                scrollBehavior = scrollBehavior,
                onMoreClicked = onMoreClicked,
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) { }
        }
    ) { paddingValues ->

        when (uiState) {
            ExperimentListScreenUiState.Loading -> ExperimentListLoadingContent(
                modifier = Modifier.padding(paddingValues),
            )

            is ExperimentListScreenUiState.Error -> ExperimentListErrorContent(
                modifier = Modifier.padding(paddingValues),
                message = uiState.message,
            )

            is ExperimentListScreenUiState.Success -> ExperimentListSuccessContent(
                modifier = Modifier.padding(paddingValues),
                experiments = uiState.experiments,
                onItemClicked = onItemClicked,
            )
        }

        if (showNewExperimentBottomSheet) {
            NewExperimentBottomSheet(
                sheetStateState = newExperimentSheetState,
                onDismissRequest = {
                    showNewExperimentBottomSheet = false
                },
            )
        }
    }
}
