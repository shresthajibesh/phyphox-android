package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX
import de.rwth_aachen.phyphox.ui.theme.customColors


@Composable
fun ExperimentListSuccessContent(
    modifier: Modifier = Modifier,
    experiments: Map<String, List<PhyphoxExperimentX>>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onItemClicked: (PhyphoxExperimentX) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        segmentedList(
            experiments = experiments,
            onExperimentClicked = onItemClicked,
        )
    }
}


fun LazyListScope.segmentedList(
    experiments: Map<String, List<PhyphoxExperimentX>>,
    onExperimentClicked: (PhyphoxExperimentX) -> Unit,
) {
    experiments.forEach { (category, items) ->
        stickyHeader(category) {
            ListHeader(title = category)
        }

        items.forEach { experiment ->
            item(experiment.title) {
                ExperimentListItem(experiment = experiment, onItemClicked = onExperimentClicked)
            }
        }
    }
}

@Composable
fun ListHeader(modifier: Modifier = Modifier, title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, bottom = 12.dp),
    )
}

@Composable
fun ExperimentListItem(
    modifier: Modifier = Modifier,
    experiment: PhyphoxExperimentX,
    onItemClicked: (PhyphoxExperimentX) -> Unit,
) {

    ListItem(
        headlineContent = {
            Text(
                text = experiment.title?.trim()?.clean() ?: "Unnamed",
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        supportingContent = {
            experiment.description?.let {
                Text(
                    text = it.trim().clean(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                )
            }
        },
        leadingContent = {
            Box(Modifier.size(40.dp)) {
                experiment.icon?.let { icon ->
                    Image(
                        bitmap = decodeBase64(icon.value).asImageBitmap(),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.customColors.primary),
                        modifier = Modifier
                            .align(Alignment.Center),
                    )

                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
        ),
        modifier = modifier.clickable { onItemClicked(experiment) },
    )
}

fun decodeBase64(input: String): Bitmap {
    val decodedByte = android.util.Base64.decode(input, 0) //Decode the base64 data to binary
    return BitmapFactory.decodeByteArray(
        decodedByte,
        0,
        decodedByte.size,
    )
}

fun String.clean(): String {
    return this.replace("\n", "")
        .replace("\t", "")
        .replace("  ", "")
}
