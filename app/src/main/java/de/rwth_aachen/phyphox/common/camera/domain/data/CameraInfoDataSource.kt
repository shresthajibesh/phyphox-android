package de.rwth_aachen.phyphox.common.camera.domain.data

import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfoList

interface CameraInfoDataSource {
    suspend fun upsertCameraInfo(cameraInfo: CameraInfo)
    suspend fun upsertCameraInfos(cameraInfos: CameraInfoList)
    suspend fun getCameraInfo(cameraId: String): Result<CameraInfo>
}
