package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage

import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.GetSupportedLanguagesUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.ObserveCurrentAppLanguageUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.UpdateAppLanguageUseCase
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.UiBuilder
import de.rwth_aachen.phyphox.ui.string.StringUIModel
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import java.util.Locale
import javax.inject.Inject

internal class DefaultAppLanguageDelegate @Inject constructor(
    private val observeCurrentAppLanguage: ObserveCurrentAppLanguageUseCase,
    private val getSupportedLanguages: GetSupportedLanguagesUseCase,
    private val updateAppLanguage: UpdateAppLanguageUseCase,
    private val uiBuilder: UiBuilder,
) : AppLanguageDelegate {
    private val currentLanguageFlow = MutableStateFlow<UiResourceState<Locale>>(UiResourceState.Loading)
    override val appLanguageFlow: Flow<UiResourceState<StringUIModel>> = currentLanguageFlow.map { localeResource ->
        uiBuilder.buildLanguageUiModel(localeResource)
    }

    override fun start(scope: CoroutineScope) {
        observeCurrentAppLanguage().onStart {
            currentLanguageFlow.value = UiResourceState.Loading
        }.onEach {
            currentLanguageFlow.value = UiResourceState.Success(it)
        }.launchIn(scope)
    }

    override suspend fun showLanguagePickerModal() {

    }
}
