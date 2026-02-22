package de.rwth_aachen.phyphox.common.camera.data.converter

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Build
import android.util.Range
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraCapability
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.FpsRange
import de.rwth_aachen.phyphox.common.camera.domain.model.HardwareLevel
import de.rwth_aachen.phyphox.common.camera.domain.model.HighSpeedVideoSizeConfiguration
import de.rwth_aachen.phyphox.common.camera.domain.model.LensFacing
import de.rwth_aachen.phyphox.common.camera.domain.model.PartialCameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.Size
import de.rwth_aachen.phyphox.common.camera.domain.model.StreamConfiguration

fun CameraCharacteristics.toDomain(id: String): CameraInfo {
    return CameraInfo(
        id = id,
        lensFacing = cameraCharacteristicsToLensFacing(
            characteristics = this.get(CameraCharacteristics.LENS_FACING),
        ),
        hardwareLevel = cameraCharacteristicsToHardwareLevel(
            characteristics = this.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL),
        ),
        capabilities = cameraCharacteristicsToCapabilities(
            capabilityIds = this.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES),
        ),
        captureRequestKeys = availableCaptureRequestKeysToCaptureRequestKeys(
            availableCaptureRequestKeys = this.availableCaptureRequestKeys,
        ),
        captureResultKeys = availableCaptureResultKeysToCaptureResultKeys(
            availableCaptureResultKeys = this.availableCaptureResultKeys,
        ),
        fpsRanges = availableTargetFpsRangesToFpsRanges(
            ranges = this.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES),
        ),
        physicalCamIds = this.getPhysicalCameraIdLists(),
        streamConfigurations = scalerStreamConfigToStreamConfigurations(
            configMap = this.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP),
        ),
    )
}

fun CameraCharacteristics.toPartialDomain(id: String): PartialCameraInfo {
    return PartialCameraInfo(
        id = id,
        lensFacing = cameraCharacteristicsToLensFacing(
            characteristics = this.get(CameraCharacteristics.LENS_FACING),
        ),
        hardwareLevel = cameraCharacteristicsToHardwareLevel(
            characteristics = this.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL),
        ),
        capabilities = cameraCharacteristicsToCapabilities(
            capabilityIds = this.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES),
        ),
    )
}

fun scalerStreamConfigToStreamConfigurations(configMap: StreamConfigurationMap?): List<StreamConfiguration> {
    if (configMap == null) {
        return emptyList()
    }
    return configMap.outputFormats.map { format ->
        val outputSizes = configMap.getOutputSizes(format).map { outPutSize ->
            outPutSizeToSize(outPutSize)
        }

        val highSpeedVideoSizes = configMap.highSpeedVideoSizes.map { highSpeedVideoSize ->
            highSpeedVideoSizeToHighSpeedVideoSizeConfig(
                highSpeedVideoSize = highSpeedVideoSize,
                fpsRanges = configMap.getHighSpeedVideoFpsRangesFor(highSpeedVideoSize),
            )
        }

        StreamConfiguration(
            format = format,
            outputSizes = outputSizes,
            highSpeedVideoSize = highSpeedVideoSizes,
        )
    }
}

fun highSpeedVideoSizeToHighSpeedVideoSizeConfig(
    highSpeedVideoSize: android.util.Size,
    fpsRanges: Array<Range<Int>>,
): HighSpeedVideoSizeConfiguration {
    return HighSpeedVideoSizeConfiguration(
        width = highSpeedVideoSize.width,
        height = highSpeedVideoSize.height,
        fpsRanges = availableTargetFpsRangesToFpsRanges(fpsRanges),
    )
}

fun outPutSizeToSize(outPutSize: android.util.Size): Size {
    return Size(
        width = outPutSize.width,
        height = outPutSize.height,
    )
}

fun availableTargetFpsRangesToFpsRanges(ranges: Array<Range<Int>>?): List<FpsRange> {
    return ranges?.map { range ->
        FpsRange(range.lower, range.upper)
    } ?: emptyList()
}

fun availableCaptureResultKeysToCaptureResultKeys(availableCaptureResultKeys: MutableList<CaptureResult.Key<*>>): List<String> {
    return availableCaptureResultKeys.map { it.name }
}

fun capabilityIdToCapability(capabilityId: Int?): CameraCapability {
    return when (capabilityId) {
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_RAW
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES
        else -> CameraCapability.REQUEST_AVAILABLE_CAPABILITIES_UNKNOWN
    }
}

fun cameraCharacteristicsToCapabilities(capabilityIds: IntArray?): List<CameraCapability> {
    return capabilityIds?.map {
        capabilityIdToCapability(it)
    } ?: emptyList()
}

fun availableCaptureRequestKeysToCaptureRequestKeys(availableCaptureRequestKeys: MutableList<CaptureRequest.Key<*>>): List<String> {
    return availableCaptureRequestKeys.map { it.name }
}

fun CameraCharacteristics.getPhysicalCameraIdLists(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) this.physicalCameraIds.toList() else listOf()
}

fun cameraCharacteristicsToLensFacing(characteristics: Int?): LensFacing {
    return when (characteristics) {
        CameraCharacteristics.LENS_FACING_FRONT -> LensFacing.LENS_FACING_FRONT
        CameraCharacteristics.LENS_FACING_BACK -> LensFacing.LENS_FACING_BACK
        CameraCharacteristics.LENS_FACING_EXTERNAL -> LensFacing.LENS_FACING_EXTERNAL
        else -> LensFacing.UNKNOWN
    }
}

fun cameraCharacteristicsToHardwareLevel(characteristics: Int?): HardwareLevel {
    return when (characteristics) {
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> HardwareLevel.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> HardwareLevel.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> HardwareLevel.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> HardwareLevel.INFO_SUPPORTED_HARDWARE_LEVEL_3
        CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> HardwareLevel.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL
        else -> HardwareLevel.UNKNOWN
    }
}

