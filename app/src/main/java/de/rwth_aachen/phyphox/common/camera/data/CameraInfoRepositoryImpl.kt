package de.rwth_aachen.phyphox.common.camera.data

import de.rwth_aachen.phyphox.common.camera.domain.data.CameraInfoDataSource
import de.rwth_aachen.phyphox.common.camera.domain.data.CameraInfoRepository
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfoList
import javax.inject.Inject

class CameraInfoRepositoryImpl @Inject constructor(
    private val cameraInfoDataSource: CameraInfoDataSource
): CameraInfoRepository {

    override suspend fun upsertCameraInfo(cameraInfo: CameraInfo) {
        cameraInfoDataSource.upsertCameraInfo(cameraInfo)
    }

    override suspend fun upsertCameraInfos(cameraInfos: CameraInfoList) {
        cameraInfoDataSource.upsertCameraInfos(cameraInfos)
    }

    override suspend fun getCameraInfo(cameraId: String): CameraInfo? {
        return cameraInfoDataSource.getCameraInfo(cameraId)
    }

    override suspend fun getAllCameraInfos(): List<CameraInfo> {
        return cameraInfoDataSource.getAllCameraInfos()
    }
}
