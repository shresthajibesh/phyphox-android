package de.rwth_aachen.phyphox.features.settings.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rwth_aachen.phyphox.features.settings.data.DefaultAppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.data.local.DefaultLocalAppPreferencesDataSource
import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.data.local.LocalAppPreferencesDataSource
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport.AccessPortDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.accessport.DefaultAccessPortDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage.AppLanguageDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.applanguage.DefaultAppLanguageDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode.AppUiModeDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.appuimode.DefaultAppUiModeDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.graphsize.DefaultGraphSizeDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.graphsize.GraphSizeDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.proximitylock.DefaultProximityLockDelegate
import de.rwth_aachen.phyphox.features.settings.presentation.viewmodel.delegates.proximitylock.ProximityLockDelegate

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    internal abstract fun bindsAccessPortDelegate(
        implementation: DefaultAccessPortDelegate,
    ): AccessPortDelegate

    @Binds
    internal abstract fun bindsAppLanguageDelegate(
        implementation: DefaultAppLanguageDelegate,
    ): AppLanguageDelegate

    @Binds
    internal abstract fun bindsAppUiModeDelegate(
        implementation: DefaultAppUiModeDelegate,
    ): AppUiModeDelegate

    @Binds
    internal abstract fun bindsGraphSizeDelegate(
        implementation: DefaultGraphSizeDelegate,
    ): GraphSizeDelegate

    @Binds
    internal abstract fun bindsProximityLockDelegate(
        implementation: DefaultProximityLockDelegate,
    ): ProximityLockDelegate

    @Binds
    internal abstract fun bindLocalDataSource(
        implementation: DefaultLocalAppPreferencesDataSource,
    ): LocalAppPreferencesDataSource

    @Binds
    internal abstract fun bndAppPreferencesRepository(
        implementation: DefaultAppPreferencesRepository,
    ): AppPreferencesRepository


}
