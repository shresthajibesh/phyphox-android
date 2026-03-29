package de.rwth_aachen.phyphox.features.experimentlist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                ExperimentListActivityScreen(uiState) {
                    //send it to vm
//                    it.links.firstOrNull()?.let { link ->
//                        val intent = Intent(Intent.ACTION_VIEW, link.url.toUri())
//                        intent.resolveActivity(this.packageManager)
//                        startActivity(intent)
//                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentListActivityScreen(
    uiState: ExperimentListScreenUiState,
    onItemClicked: (PhyphoxExperimentX) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            MainBottomAppBar()
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
    onSettingsClicked: () -> Unit = {},
    onSearchClicked: () -> Unit = {},
) {

    // Floating Toolbar Group
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f),
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
    ) {
        var isSearchFieldVisible by remember { mutableStateOf(false) }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AnimatedVisibility(
                isSearchFieldVisible,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    TextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    IconButton(onClick = { isSearchFieldVisible = !isSearchFieldVisible }) {
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
               ){
                   IconButton(onClick = { isSearchFieldVisible = !isSearchFieldVisible }) {
                       Icon(
                           imageVector = Icons.Default.Search,
                           contentDescription = "Search",
                           tint = MaterialTheme.colorScheme.onSecondaryContainer,
                       )
                   }
                   IconButton(onClick = { /* TODO: Navigate to My Experiments */ }) {
                       Icon(
                           imageVector = Icons.Default.FilterList,
                           contentDescription = "Search",
                           tint = MaterialTheme.colorScheme.onSecondaryContainer,
                       )
                   }
                   FloatingActionButton(
                       onClick = { /* TODO: Add Experiment action */ },
                       containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                       contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                       elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
                   ) {
                       Icon(Icons.Default.Add, contentDescription = "Add")
                   }

                   IconButton(onClick = { /* TODO: Navigate to Settings */ }) {
                       Icon(
                           imageVector = Icons.Default.Settings,
                           contentDescription = "Settings",
                           tint = MaterialTheme.colorScheme.onSecondaryContainer,
                       )
                   }
               }
            }
        }
    }
}
