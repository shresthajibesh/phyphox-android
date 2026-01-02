package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

sealed interface SettingsAction {
    data class OnUiModeItemSelected(val appUiMode: UiModeUiModel) : SettingsAction
    data class OnGraphSizeChanged(val size: Float) : SettingsAction
    data class OnProximityLockChanged(val enabled: Boolean) : SettingsAction
    data object OnAppLanguageClicked : SettingsAction
    data object OnLearnMoreAboutTranslationClicked : SettingsAction
    data object OnAccessPortClicked : SettingsAction
    data object OnBackPressed : SettingsAction
}
