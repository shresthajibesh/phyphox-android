package de.rwth_aachen.phyphox.features.settings.domain.usecase.proximitylock

import javax.inject.Inject

class UpdateProximityLockStatusUseCase @Inject constructor(){
    suspend operator fun invoke(enabled: Boolean) : Result<Unit>{
        return Result.success(Unit)
    }
}
