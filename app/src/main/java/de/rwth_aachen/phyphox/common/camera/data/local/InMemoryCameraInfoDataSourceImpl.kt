package de.rwth_aachen.phyphox.common.camera.data.local

import de.rwth_aachen.phyphox.common.camera.domain.data.CameraInfoDataSource
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfoList
import de.rwth_aachen.phyphox.libs.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCameraInfoDataSourceImpl @Inject constructor(
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CameraInfoDataSource {

    private val cameraInfoMap = mutableMapOf<String, CameraInfo>()

    override suspend fun upsertCameraInfo(cameraInfo: CameraInfo) = withContext(dispatcher) {
        cameraInfoMap[cameraInfo.id] = cameraInfo
    }

    override suspend fun upsertCameraInfos(cameraInfos: CameraInfoList) = withContext(dispatcher) {
        cameraInfos.forEach { upsertCameraInfo(it) }
    }

    override suspend fun getCameraInfo(cameraId: String): CameraInfo? = withContext(dispatcher) {
        return@withContext cameraInfoMap[cameraId]
    }

    override suspend fun getAllCameraInfos(): List<CameraInfo> = withContext(dispatcher) {
        return@withContext cameraInfoMap.values.toList()
    }
}
