package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage

import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface AppLanguageDelegate {
    val appLanguageFlow: Flow<UiResourceState<StringUIModel>>
    fun start(scope: CoroutineScope)
}
