package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

sealed interface SettingsEvent {
    data class OpenWebpage(val url: String) : SettingsEvent
    data object NavigateBack : SettingsEvent
    data object NavigateToLanguagePicker : SettingsEvent
    data object ShowAccessPortModal : SettingsAction
}
