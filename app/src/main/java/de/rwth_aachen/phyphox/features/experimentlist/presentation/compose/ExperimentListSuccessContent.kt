package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX


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
        groupedListType(
            experiments = experiments,
            onExperimentClicked = onItemClicked,
        )
    }
}


fun LazyListScope.groupedListType(
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)

    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium
        )
        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun ExperimentListItem(
    modifier: Modifier = Modifier,
    experiment: PhyphoxExperimentX,
    onItemClicked: (PhyphoxExperimentX) -> Unit,
) {

    Row(
        modifier = modifier
            .clickable(enabled = true) {
                onItemClicked(experiment)
            }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            Modifier
                .size(56.dp),
        ) {
            experiment.icon?.let { icon ->
                Image(
                    bitmap = decodeBase64(icon.value).asImageBitmap(),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .align(Alignment.Center),
                )

            }
        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = experiment.title?.trim() ?: "Name Not Found",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                text = experiment.description?.trim()?.clean() ?: "Name Not Found",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        }
    }
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
