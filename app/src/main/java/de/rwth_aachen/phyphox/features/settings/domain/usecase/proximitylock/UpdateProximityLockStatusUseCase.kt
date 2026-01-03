package de.rwth_aachen.phyphox.features.settings.domain.usecase.proximitylock

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import javax.inject.Inject

class UpdateProximityLockStatusUseCase @Inject constructor(
    private val repository: AppPreferencesRepository
){
    suspend operator fun invoke(enabled: Boolean) : Result<Unit>{
        return Result.success(Unit)
    }
}
