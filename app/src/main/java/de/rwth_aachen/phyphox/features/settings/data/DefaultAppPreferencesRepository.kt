package de.rwth_aachen.phyphox.features.settings.data

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.data.local.LocalAppPreferencesDataSource
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
}
