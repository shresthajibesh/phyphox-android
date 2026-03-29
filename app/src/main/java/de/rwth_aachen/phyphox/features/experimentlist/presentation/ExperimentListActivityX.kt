package de.rwth_aachen.phyphox.features.experimentlist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.ExperimentListErrorContent
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.ExperimentListLoadingContent
import de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.ExperimentListSuccessContent
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.DisplayType
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.ExperimentListScreenUiState
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.ExperimentListViewModel
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme
import de.rwth_aachen.phyphox.utils.isDarkThemeEnabled
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExperimentListActivityX : ComponentActivity() {

    private val viewModel: ExperimentListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PhyphoxTheme(darkTheme = isDarkThemeEnabled()) {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                ExperimentListActivityScreen(
                    uiState = uiState,
                    onMoreClicked = viewModel::onMoreClicked,
                    onFilterTextChanged = viewModel::onFilterTextChanged,
                    onItemClicked = viewModel::onItemClicked,
                    onFilterCloseClicked = viewModel::onFilterCloseClicked,
                    onDisplayTypeSelected = viewModel::onDisplayTypeSelected,
                )
            }
        }
    }
}


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
    val coroutineScope = rememberCoroutineScope()
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
            ModalBottomSheet(
                onDismissRequest = {
                    showDisplayTypeBottomSheet = false
                },
                sheetState = displayTypeSheetState,
            ) {
                Column {
                    DisplayType.entries.forEach { displayType ->
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch { displayTypeSheetState.hide() }.invokeOnCompletion {
                                        if (!displayTypeSheetState.isVisible) {
                                            showDisplayTypeBottomSheet = false
                                        }
                                        onDisplayTypeSelected(displayType)
                                    }
                                }
                                .padding(16.dp),
                            text = stringResource(displayType.nameId),
                        )
                    }
                }
            }
        }
        if (showNewExperimentBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showNewExperimentBottomSheet = false
                },
                sheetState = displayTypeSheetState,
            ) {
                // Sheet content
                Button(
                    onClick = {
                        coroutineScope.launch { newExperimentSheetState.hide() }.invokeOnCompletion {
                            if (!newExperimentSheetState.isVisible) {
                                showNewExperimentBottomSheet = false
                            }
                        }
                    },
                ) {
                    Text("Hide bottom sheet")
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                stringResource(R.string.title_experiment_list),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomAppBar(
    modifier: Modifier = Modifier,
    onNewClicked: () -> Unit,
    onMoreClicked: () -> Unit,
    onDisplayTypeClicked: () -> Unit,
    onFilterCloseClicked: () -> Unit,
    onFilterTextChanged: (String) -> Unit,
) {

    // Floating Toolbar Group
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {
        var isSearchFieldVisible by remember { mutableStateOf(false) }
        var filterText by remember { mutableStateOf("") }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AnimatedVisibility(
                isSearchFieldVisible,
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = filterText,
                        onValueChange = {
                            filterText = it
                            onFilterTextChanged(it)
                        },
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    IconButton(
                        onClick = {
                            isSearchFieldVisible = !isSearchFieldVisible
                            filterText = ""
                            onFilterTextChanged("")
                            onFilterCloseClicked()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Search",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            AnimatedVisibility(!isSearchFieldVisible) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { isSearchFieldVisible = !isSearchFieldVisible }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    IconButton(onClick = onDisplayTypeClicked) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    FloatingActionButton(
                        onClick = onNewClicked,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }

                    IconButton(onClick = onMoreClicked) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }
        }
    }
}
