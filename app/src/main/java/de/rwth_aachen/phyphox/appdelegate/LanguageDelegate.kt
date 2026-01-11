package de.rwth_aachen.phyphox.appdelegate

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import de.rwth_aachen.phyphox.features.settings.domain.model.AppLanguage
import de.rwth_aachen.phyphox.features.settings.domain.usecase.language.ObserveCurrentAppLanguageUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


class LanguageDelegate @Inject constructor(
    private val observeCurrentAppLanguageUseCase: ObserveCurrentAppLanguageUseCase,
) {
    fun start(scope: CoroutineScope) {
        observeCurrentAppLanguageUseCase()
            .onEach { appLanguage ->
                val localeList =
                    if (appLanguage.identifier == AppLanguage.SYSTEM_DEFAULT_IDENTIFIER) {        // Use system default if the code is empty
                        LocaleListCompat.getEmptyLocaleList()
                    } else {
                        LocaleListCompat.forLanguageTags(appLanguage.identifier)
                    }
                AppCompatDelegate.setApplicationLocales(localeList)
            }.launchIn(scope)
    }
}
