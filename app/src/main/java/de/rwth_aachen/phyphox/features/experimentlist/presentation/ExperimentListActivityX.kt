package de.rwth_aachen.phyphox.features.experimentlist.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentListActivityScreen(uiState: ExperimentListScreenUiState) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(scrollBehavior = scrollBehavior)
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            val contentModifier = Modifier.fillMaxSize()

            when (uiState) {
                ExperimentListScreenUiState.Loading -> ExperimentListLoadingContent(
                    modifier = contentModifier.padding(paddingValues),
                )

                is ExperimentListScreenUiState.Error -> ExperimentListErrorContent(
                    modifier = contentModifier.padding(paddingValues),
                    message = uiState.message,
                )

                is ExperimentListScreenUiState.Success -> ExperimentListSuccessContent(
                    modifier = contentModifier,
                    experiments = uiState.experiments,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = paddingValues.calculateTopPadding() + 16.dp,
                        bottom = 112.dp, // Padding to avoid content being hidden behind the floating bar
                    ),
                )
            }

            MainBottomAppBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
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
    MediumTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                "Medium Top App Bar",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Localized description",
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun MainBottomAppBar(modifier: Modifier = Modifier) {
    var showFab by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Floating Toolbar Group
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.95f),
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        showFab = !showFab
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Experiments List",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
                IconButton(onClick = { /* TODO: Navigate to My Experiments */ }) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "My Experiments",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
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

        AnimatedVisibility(
            visible = showFab,
            enter = fadeIn() + expandHorizontally() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
        ) {
            FloatingActionButton(
                onClick = { /* TODO: Add Experiment action */ },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

    }
}
