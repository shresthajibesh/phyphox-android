package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage

import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.util.Locale

interface AppLanguageDelegate {
    val appLanguageFlow: Flow<UiResourceState<Locale>>
    fun start(scope: CoroutineScope)
}
