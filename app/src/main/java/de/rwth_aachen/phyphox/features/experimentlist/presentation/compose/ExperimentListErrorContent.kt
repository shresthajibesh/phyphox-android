package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import de.rwth_aachen.phyphox.ui.theme.PhyphoxTheme


@Composable
fun ExperimentListErrorContent(modifier: Modifier = Modifier, message: String) {
    Row(modifier = modifier.fillMaxSize()) {
        Text("Shit something went wrong, $message")
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ExperimentListErrorContentPreview() {
    PhyphoxTheme {
        ExperimentListErrorContent(message = LoremIpsum(12).values.first())
    }
}
