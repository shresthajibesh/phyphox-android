package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage

import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.GetSupportedLanguagesUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.ObserveCurrentAppLanguageUseCase
import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.UpdateAppLanguageUseCase
import de.rwth_aachen.phyphox.utils.UiResourceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

class DefaultAppLanguageDelegate @Inject constructor(
    private val observeCurrentAppLanguage: ObserveCurrentAppLanguageUseCase,
    private val getSupportedLanguages: GetSupportedLanguagesUseCase,
    private val updateAppLanguage: UpdateAppLanguageUseCase
) : AppLanguageDelegate {
    private val currentLanguageFlow = MutableStateFlow<UiResourceState<Locale>>(UiResourceState.Loading)
    override val appLanguageFlow: Flow<UiResourceState<Locale>> = currentLanguageFlow.asStateFlow()
    override fun start(scope: CoroutineScope) {
        TODO("Not yet implemented")
    }
}
