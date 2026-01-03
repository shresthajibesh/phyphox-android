package de.rwth_aachen.phyphox.features.settings.domain.data.local

import kotlinx.coroutines.flow.Flow

interface LocalAppPreferencesDataSource :
    LocalAccessPortPreferencesDataSource,
    LocalGraphSizePreferencesDataSource,
    LocalLanguagePreferencesDataSource,
    LocalProximityLockPreferencesDataSource,
    LocalUiConfigPreferencesDataSource {

}

