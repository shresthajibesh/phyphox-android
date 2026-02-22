package de.rwth_aachen.phyphox.common.camera.domain.interactor

import de.rwth_aachen.phyphox.common.camera.domain.data.CameraInfoRepository
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfoList
import javax.inject.Inject

class UpsertCameraInfo @Inject constructor(
    val repository: CameraInfoRepository,
) {
    suspend operator fun invoke(cameraInfo: CameraInfo) {
        repository.upsertCameraInfo(cameraInfo)
    }

    suspend operator fun invoke(cameraInfos: CameraInfoList) {
        repository.upsertCameraInfos(cameraInfos)
    }

    suspend operator fun invoke(vararg cameraInfos: CameraInfo) {
        repository.upsertCameraInfos(cameraInfos.toList())
    }
}
