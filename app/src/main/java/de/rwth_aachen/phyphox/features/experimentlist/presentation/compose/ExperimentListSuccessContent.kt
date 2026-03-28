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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.features.experimentlist.domain.model.PhyphoxExperimentX


@Composable
fun ExperimentListSuccessContent(
    modifier: Modifier = Modifier,
    experiments: List<PhyphoxExperimentX>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onItemClicked: (PhyphoxExperimentX) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        items(experiments) {
            ExperimentListItem(experiment = it, onItemClicked = onItemClicked)
        }
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
                .size(56.dp)
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
