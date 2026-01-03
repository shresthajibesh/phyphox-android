package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport

import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface AccessPortDelegate {
    val accessPortFlow: Flow<UiResourceState<StringUIModel>>
    fun start(scope: CoroutineScope)

    suspend fun showAccessPortInputModal()
}

