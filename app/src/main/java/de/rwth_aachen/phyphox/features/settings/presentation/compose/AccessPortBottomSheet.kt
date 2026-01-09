package de.rwth_aachen.phyphox.features.settings.presentation.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.R // Assuming your string resources are here
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport.AccessPortSheetUiModel
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.ui.string.resolve
import de.rwth_aachen.phyphox.ui.string.toStringUIModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessPortBottomSheet(
    uiModel: AccessPortSheetUiModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val initial = uiModel.currentPort.resolve()
    var textState by remember(uiModel.currentPort) {
        mutableStateOf(initial)
    }

    val isError = uiModel.error != null
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Title for the bottom sheet
            Text(
                text = "Title",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Text input field
            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it},
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Port") },
                isError = isError,
                supportingText = {
                    uiModel.error?.let {
                        Text(text = it.resolve())
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            TextButton(
                onClick = {
                    if (!isError) {
                        onConfirm(textState)
                    }
                },
                modifier = Modifier.align(Alignment.End),
                enabled = !isError // Disable confirm button if there's an error
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AccessPortBottomSheetPreview() {
    // State for showing the bottom sheet in the preview
    val sheetState = rememberModalBottomSheetState()

    // Example with valid input
    val uiModel = AccessPortSheetUiModel(
        currentPort = "8080".toStringUIModel(),
        range = 1024..65535,
        error = "Some error".toStringUIModel()
    )

    // Example with an error state (uncomment to see)
    /*
    val uiModelWithError = AccessPortSheetUiModel(
        currentPort = StringUIModel.StaticString("100"), // Invalid initial value
        range = 1024..65535,
        error = StringUIModel.StringResource(R.string.settings_we_access_port_invalid, 1024, 65535)
    )
    */

    AccessPortBottomSheet(
        uiModel = uiModel,
        onDismiss = {},
        onConfirm = {},
        sheetState = sheetState
    )
}
