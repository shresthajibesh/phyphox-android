package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates


import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.ResourceState
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

interface LanguageSettingsDelegate {
    val languageStateFlow: StateFlow<ResourceState<Locale>>
}



