package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import de.rwth_aachen.phyphox.ui.string.StringUIModel

data class SettingsUiState(
    val currentLanguage: ResourceState<StringUIModel> = ResourceState.Loading,
    val graphSize: ResourceState<Int> = ResourceState.Loading,
    val accessPort: ResourceState<Int> = ResourceState.Loading,
    val proximityLockEnabled: ResourceState<Boolean> = ResourceState.Loading,
)

sealed interface ResourceState<out T> {
    data object Loading : ResourceState<Nothing>
    data class Success<T>(val data: T) : ResourceState<T>
}
