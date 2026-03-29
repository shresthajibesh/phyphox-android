package de.rwth_aachen.phyphox.features.experimentlist.presentation.compose.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.features.experimentlist.presentation.viewmodel.DisplayType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayTypeSheet(
    modifier: Modifier = Modifier,
    sheetStateState: SheetState = rememberModalBottomSheetState(),
    onDismissRequest: () -> Unit,
    onDisplayTypeSelected: (DisplayType) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetStateState,
    ) {
        Column {
            DisplayType.entries.forEach { displayType ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch { sheetStateState.hide() }.invokeOnCompletion {
                                if (!sheetStateState.isVisible) {
                                    onDismissRequest()
                                }
                                onDisplayTypeSelected(displayType)
                            }
                        }
                        .padding(16.dp),
                    text = stringResource(displayType.nameId),
                )
            }
        }
    }
}
