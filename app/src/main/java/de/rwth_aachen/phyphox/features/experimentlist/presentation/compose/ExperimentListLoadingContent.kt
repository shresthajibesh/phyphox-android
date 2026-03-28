package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme

@Composable
fun ExperimentListLoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ExperimentListLoadingContentPreview() {
    PhyphoxTheme {
        ExperimentListLoadingContent()
    }
}
