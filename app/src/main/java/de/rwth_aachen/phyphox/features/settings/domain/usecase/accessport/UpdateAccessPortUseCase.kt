package de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import javax.inject.Inject

class UpdateAccessPortUseCase @Inject constructor(
    private val repository: AppPreferencesRepository,
) {
    suspend operator fun invoke(port: Int): Result<Unit> {
        return try {
            repository.updateAccessPort(port)
            Result.success(Unit)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }
}
