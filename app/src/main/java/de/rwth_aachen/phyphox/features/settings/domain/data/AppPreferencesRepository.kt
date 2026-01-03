package de.rwth_aachen.phyphox.features.settings.domain.data

import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    fun observeCurrentAccessPort(): Flow<Int?>
    suspend fun updateAccessPort(port: Int)
}
