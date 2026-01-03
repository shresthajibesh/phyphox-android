package de.rwth_aachen.phyphox.features.settings.data.local

import de.rwth_aachen.phyphox.features.settings.domain.data.local.LocalAppPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class DefaultLocalAppPreferencesDataSource: LocalAppPreferencesDataSource {
    override fun observeCurrentAccessPort(): Flow<Int?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateAccessPort(port: Int) {
        TODO("Not yet implemented")
    }
}
