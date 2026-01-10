package de.rwth_aachen.phyphox.features.settings.presentation.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.features.settings.domain.model.AppLanguage
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage.AppLanguageSheetUiModel
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage.LanguageUiModel
import de.rwth_aachen.phyphox.ui.string.resolve
import kotlin.collections.sortedWith

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppLanguageBottomSheet(
    uiModel: AppLanguageSheetUiModel,
    onDismiss: () -> Unit,
    onConfirm: (identifier:String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            stickyHeader {
                Text(
                    text = stringResource(R.string.settingsLanguage),
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                )
            }
            items(uiModel.availableLocales) { item ->
                Text(item.displayName.resolve())
            }
        }
    }
}
