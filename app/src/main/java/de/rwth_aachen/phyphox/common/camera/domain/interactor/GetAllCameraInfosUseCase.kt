package de.rwth_aachen.phyphox.common.camera.domain.interactor

import de.rwth_aachen.phyphox.common.camera.domain.data.CameraInfoRepository
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import javax.inject.Inject

class GetAllCameraInfosUseCase @Inject constructor(
    val repository: CameraInfoRepository,
) {

    suspend operator fun invoke(cameraId: String): Result<Result<List<CameraInfo>>> {
        return try {
            Result.success(repository.getAllCameraInfos())
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
