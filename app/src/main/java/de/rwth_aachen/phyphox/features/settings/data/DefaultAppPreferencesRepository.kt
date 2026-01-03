package de.rwth_aachen.phyphox.features.settings.data

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.data.local.LocalAppPreferencesDataSource
import de.rwth_aachen.phyphox.features.settings.domain.model.AppUiMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class DefaultAppPreferencesRepository @Inject constructor(
    private val localAppPreferencesDataSource: LocalAppPreferencesDataSource
) : AppPreferencesRepository{
    override fun observeCurrentAccessPort(): Flow<Int?> {
        return localAppPreferencesDataSource.observeCurrentAccessPort()
    }

    override suspend fun updateAccessPort(port: Int) {
        return localAppPreferencesDataSource.updateAccessPort(port)
    }

    override fun observeGraphSize(): Flow<Float?> {
        return localAppPreferencesDataSource.observeGraphSize()
    }

    override suspend fun updateGraphSize(size: Float) {
        localAppPreferencesDataSource.updateGraphSize(size)
    }

    override fun observeLanguage(): Flow<String?> {
        return localAppPreferencesDataSource.observeLanguage()
    }

    override suspend fun updateLanguage(language: String) {
        localAppPreferencesDataSource.updateLanguage(language)
    }

    override fun observeProximityLockEnabled(): Flow<Boolean?> {
        return localAppPreferencesDataSource.observeProximityLockEnabled()
    }

    override suspend fun updateProximityLockEnabled(enabled: Boolean) {
        localAppPreferencesDataSource.updateProximityLockEnabled(enabled)
    }

    override fun observeAppUiMode(): Flow<AppUiMode?> {
        return localAppPreferencesDataSource.observeAppUiMode()
    }

    override suspend fun updateAppUiMode(mode: AppUiMode) {
        localAppPreferencesDataSource.updateAppUiMode(mode)
    }
}
