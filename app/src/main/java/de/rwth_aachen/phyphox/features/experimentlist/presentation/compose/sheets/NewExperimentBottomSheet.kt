package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.sheets


import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewExperimentBottomSheet(
    modifier: Modifier = Modifier,
    sheetStateState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetStateState,
    ) {
        // Sheet content
        Button(
            onClick = {
                coroutineScope.launch { sheetStateState.hide() }.invokeOnCompletion {
                    if (!sheetStateState.isVisible) {
                        onDismissRequest()
                    }
                }
            },
        ) {
            Text("Hide bottom sheet")
        }
    }
}
