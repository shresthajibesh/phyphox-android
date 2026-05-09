package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val newExperimentSheetState = rememberModalBottomSheetState()
    var showNewExperimentBottomSheet by remember { mutableStateOf(false) }

    var isFabVisible by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        var previousIndex = listState.firstVisibleItemIndex
        var previousScrollOffset = listState.firstVisibleItemScrollOffset
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val isScrollingDown = if (index != previousIndex) {
                    index > previousIndex
                } else {
                    offset > previousScrollOffset
                }

                isFabVisible = !isScrollingDown
                previousIndex = index
                previousScrollOffset = offset
            }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                scrollBehavior = scrollBehavior,
                onMoreClicked = onMoreClicked,
            )
        },
        floatingActionButton = {
            AnimatedVisibility(isFabVisible) {
                FloatingActionButton(
                    onClick = {
                        showNewExperimentBottomSheet = true
                    },
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
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
                onItemClicked = onItemClicked,
                listState = listState
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
