package de.rwth_aachen.phyphox.ui.string

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

val emptyStringUiModel = "".toStringUIModel()

fun String.toStringUIModel(): StringUIModel = TextStringUIModel(this)
fun String?.toStringOrEmptyUIModel(): StringUIModel = this?.toStringUIModel() ?: emptyStringUiModel

fun @receiver:StringRes Int.toStringUIModel(
    vararg args: Any
): StringUIModel = ResourceStringUIModel(
    resId = this,
    formatArgs = args
)

@Composable
fun StringUIModel.resolve() = resolve(LocalContext.current)


@Composable
fun StringUIModel?.resolveOrDefault(default: String = ""): String = this?.resolve() ?: default
