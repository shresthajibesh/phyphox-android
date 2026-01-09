package de.rwth_aachen.phyphox.features.settings.domain.usecase.accessport

import de.rwth_aachen.phyphox.features.settings.domain.data.AppPreferencesRepository
import de.rwth_aachen.phyphox.features.settings.domain.model.errors.AccessPortOutOfRange
import javax.inject.Inject

class UpdateAccessPortUseCase @Inject constructor(
    private val repository: AppPreferencesRepository,
    private val getAccessPortRange: GetAccessPortRangeApplicationService,
) {
    suspend operator fun invoke(port: Int): Result<Unit> {
        return try {
            if (!isPortWithinRange(port)){
                Result.failure(AccessPortOutOfRange())
            }else{
                repository.updateAccessPort(port)
                Result.success(Unit)
            }

        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    suspend operator fun invoke(port: String): Result<Unit> {
        return try {
            val number = port.toInt()
            invoke(number)
        } catch (error: NumberFormatException) {
            Result.failure(error)
        }
    }

    private fun isPortWithinRange(port: Int): Boolean {
        val validRange = getAccessPortRange()
        return validRange.contains(port)
    }
}
