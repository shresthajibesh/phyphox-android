package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.appbar.MainBottomAppBar
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.appbar.MainTopAppBar
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.sheets.DisplayTypeSheet
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.sheets.NewExperimentBottomSheet
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.DisplayType
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.ExperimentListScreenUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentListActivityScreen(
    uiState: ExperimentListScreenUiState,
    onMoreClicked: () -> Unit,
    onFilterTextChanged: (String) -> Unit,
    onFilterCloseClicked: () -> Unit,
    onItemClicked: (PhyphoxExperimentX) -> Unit,
    onDisplayTypeSelected: (DisplayType) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val displayTypeSheetState = rememberModalBottomSheetState()
    var showDisplayTypeBottomSheet by remember { mutableStateOf(false) }

    val newExperimentSheetState = rememberModalBottomSheetState()
    var showNewExperimentBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            MainBottomAppBar(
                onMoreClicked = onMoreClicked,
                onDisplayTypeClicked = {
                    showDisplayTypeBottomSheet = true
                },
                onFilterTextChanged = onFilterTextChanged,
                onNewClicked = {
                    showNewExperimentBottomSheet = true
                },
                onFilterCloseClicked = {
                    showDisplayTypeBottomSheet = false
                    onFilterCloseClicked()
                },
            )
        },
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
                displayType = uiState.displayType,
                onItemClicked = onItemClicked,
            )
        }

        if (showDisplayTypeBottomSheet) {
            DisplayTypeSheet(
                sheetStateState = displayTypeSheetState,
                onDismissRequest = {
                    showDisplayTypeBottomSheet = false
                },
                onDisplayTypeSelected = onDisplayTypeSelected,
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
