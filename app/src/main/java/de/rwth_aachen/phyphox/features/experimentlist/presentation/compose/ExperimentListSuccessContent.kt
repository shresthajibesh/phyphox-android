package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import de.rwth_aachen.phyphox.ui.theme.customColors


@Composable
fun ExperimentListSuccessContent(
    modifier: Modifier = Modifier,
    experiments: List<PhyphoxExperimentX>,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
    ) {
        items(experiments) {
            ExperimentListItem(experiment = it)
        }
    }
}

@Composable
fun ExperimentListItem(
    modifier: Modifier = Modifier,
    experiment: PhyphoxExperimentX,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(48.dp)
        ) {
            experiment.icon?.let { icon ->
                Image(
                    bitmap = decodeBase64(icon.value).asImageBitmap(),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier.size(32.dp).align(Alignment.Center)
                )

            }
        }
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = experiment.title?.trim() ?: "Name Not Found",
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                text = experiment.description?.trim()?.clean() ?: "Name Not Found",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
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
