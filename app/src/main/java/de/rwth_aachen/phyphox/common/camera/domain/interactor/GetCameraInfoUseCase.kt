package de.rwth_aachen.phyphox.common.camera.domain.interactor

import de.rwth_aachen.phyphox.common.camera.domain.data.CameraInfoRepository
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.error.CameraInfoNotFound
import javax.inject.Inject

class GetCameraInfoUseCase @Inject constructor(
    val repository: CameraInfoRepository,
) {
    suspend operator fun invoke(cameraId: String): Result<CameraInfo> {
        return try {
            repository.getCameraInfo(cameraId)?.let {
                Result.success(it)
            } ?: Result.failure(CameraInfoNotFound(cameraId))

        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
