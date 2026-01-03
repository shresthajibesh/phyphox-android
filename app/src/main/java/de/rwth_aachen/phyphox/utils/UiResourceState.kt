package de.rwth_aachen.phyphox.utils

sealed interface UiResourceState<out T> {
    data object Loading : UiResourceState<Nothing>
    data class Success<T>(val data: T) : UiResourceState<T>
}
