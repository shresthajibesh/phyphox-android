package de.rwth_aachen.phyphox.features.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport.AccessPortDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage.AppLanguageDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode.AppUiModeDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.graphsize.GraphSizeDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.proximitylock.ProximityLockDelegate
import de.rwth_aachen.phyphox.utils.UIEventFlow
import de.rwth_aachen.phyphox.utils.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val accessPortDelegate: AccessPortDelegate,
    private val appLanguageDelegate: AppLanguageDelegate,
    private val appUiModeDelegate: AppUiModeDelegate,
    private val graphSizeDelegate: GraphSizeDelegate,
    private val proximityLockDelegate: ProximityLockDelegate,
) : ViewModel(),
    AccessPortDelegate by accessPortDelegate,
    AppLanguageDelegate by appLanguageDelegate,
    AppUiModeDelegate by appUiModeDelegate,
    GraphSizeDelegate by graphSizeDelegate,
    ProximityLockDelegate by proximityLockDelegate {


    private val _uiEvent = UIEventFlow<SettingsEvent>()
    val uiEvent = _uiEvent.asFlow()

    init {
        start(viewModelScope)
    }

    override fun start(scope: CoroutineScope) {
        accessPortDelegate.start(scope)
        appLanguageDelegate.start(scope)
        appUiModeDelegate.start(scope)
        graphSizeDelegate.start(scope)
    }

    val uiState = combine(
        accessPortDelegate.accessPortFlow,
        appLanguageDelegate.appLanguageFlow,
        appUiModeDelegate.appUiModeFlow,
        graphSizeDelegate.graphSizeFlow,
        proximityLockDelegate.proximityLockFlow,
    ) { accessPort, currentLanguage, uiMode, graphSize, proximityLockEnabled ->

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(),
    )

    fun onActionEvent(action: SettingsAction) {

    }
}
