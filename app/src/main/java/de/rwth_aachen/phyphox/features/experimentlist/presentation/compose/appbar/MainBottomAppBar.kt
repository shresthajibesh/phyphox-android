package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.appbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
