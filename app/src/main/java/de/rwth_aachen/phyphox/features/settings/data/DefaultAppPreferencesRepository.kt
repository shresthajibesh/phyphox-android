package de.rwth_aachen.phyphox.features.settings.data

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.data.local.LocalAppPreferencesDataSource
import javax.inject.Inject


class DefaultAppPreferencesRepository @Inject constructor(
    private val localAppPreferencesDataSource: LocalAppPreferencesDataSource
) : AppPreferencesRepository{

}
