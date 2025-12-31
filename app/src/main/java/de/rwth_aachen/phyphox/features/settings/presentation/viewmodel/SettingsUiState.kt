package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import de.rwth_aachen.phyphox.ui.string.StringUIModel

data class SettingsUiState(
    val currentLanguage: ResourceState<StringUIModel> = ResourceState.Loading,
    val graphSize: ResourceState<SeekBarConfig> = ResourceState.Loading,
    val uiMode: ResourceState<StringUIModel> = ResourceState.Loading,
//    val dynamicTheme: ResourceState<Boolean> = ResourceState.Loading,
    val accessPort: ResourceState<StringUIModel> = ResourceState.Loading,
    val proximityLockEnabled: ResourceState<Boolean> = ResourceState.Loading,

)

sealed interface ResourceState<out T> {
    data object Loading : ResourceState<Nothing>
    data class Success<T>(val data: T) : ResourceState<T>
}

fun ResourceState<Boolean>.isChecked(): Boolean {
    return this is ResourceState.Success && this.data
}

data class SeekBarConfig(
    val currentSize: Float,
    val range: ClosedFloatingPointRange<Float>,
) {
    constructor(
        currentSize: Float,
        minSize: Float,
        maxSize: Float,
    ): this(
        currentSize = currentSize,
        range = minSize..maxSize,
    )
}
