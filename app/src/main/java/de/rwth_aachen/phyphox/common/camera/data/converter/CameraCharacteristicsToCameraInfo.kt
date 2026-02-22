package de.rwth_aachen.phyphox.common.camera.data.converter

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Build
import android.util.Range
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.Capability
import de.rwth_aachen.phyphox.common.camera.domain.model.FpsRange
import de.rwth_aachen.phyphox.common.camera.domain.model.HardwareLevel
import de.rwth_aachen.phyphox.common.camera.domain.model.HighSpeedVideoSizeConfiguration
import de.rwth_aachen.phyphox.common.camera.domain.model.LensFacing
import de.rwth_aachen.phyphox.common.camera.domain.model.Size
import de.rwth_aachen.phyphox.common.camera.domain.model.StreamConfiguration

class CameraCharacteristicsToCameraInfo {

    fun convert(
        id: String,
        item: CameraCharacteristics,
    ): CameraInfo {
        return CameraInfo(
            id = id,
            lensFacing = cameraCharacteristicsToLensFacing(item.get(CameraCharacteristics.LENS_FACING)),
            hardwareLevel = cameraCharacteristicsToHardwareLevel(item.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)),
            capabilities = cameraCharacteristicsToCapabilities(item.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)),
            captureRequestKeys = availableCaptureRequestKeysToCaptureRequestKeys(item.availableCaptureRequestKeys),
            captureResultKeys = availableCaptureResultKeysToCaptureResultKeys(item.availableCaptureResultKeys),
            fpsRanges = availableTargetFpsRangesToFpsRanges(item.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)),
            physicalCamIds = item.getPhysicalCameraIdLists(),
            streamConfigurations = scalerStreamConfigToStreamConfigurations(item.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)),
        )
    }

    private fun scalerStreamConfigToStreamConfigurations(configMap: StreamConfigurationMap?): List<StreamConfiguration> {
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
                    fpsRanges = configMap.getHighSpeedVideoFpsRangesFor(highSpeedVideoSize)
                )
            }

            StreamConfiguration(
                format = format,
                outputSizes = outputSizes,
                highSpeedVideoSize = highSpeedVideoSizes,
            )
        }
    }

    private fun highSpeedVideoSizeToHighSpeedVideoSizeConfig(
        highSpeedVideoSize: android.util.Size,
        fpsRanges: Array<Range<Int>>,
    ): HighSpeedVideoSizeConfiguration {
        return HighSpeedVideoSizeConfiguration(
            width = highSpeedVideoSize.width,
            height = highSpeedVideoSize.height,
            fpsRanges = availableTargetFpsRangesToFpsRanges(fpsRanges),
        )
    }

    private fun outPutSizeToSize(outPutSize: android.util.Size): Size {
        return Size(
            width = outPutSize.width,
            height = outPutSize.height,
        )
    }
}


fun availableTargetFpsRangesToFpsRanges(ranges: Array<Range<Int>>?): List<FpsRange> {
    return ranges?.map { range ->
        FpsRange(range.lower, range.upper)
    } ?: emptyList()
}

fun availableCaptureResultKeysToCaptureResultKeys(availableCaptureResultKeys: MutableList<CaptureResult.Key<*>>): List<String> {
    return availableCaptureResultKeys.map { it.name }
}

fun capabilityIdToCapability(capabilityId: Int?): Capability {
    return when (capabilityId) {
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE -> Capability.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR -> Capability.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING -> Capability.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW -> Capability.REQUEST_AVAILABLE_CAPABILITIES_RAW
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING -> Capability.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS -> Capability.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE -> Capability.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING -> Capability.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT -> Capability.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO -> Capability.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING -> Capability.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA -> Capability.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME -> Capability.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA -> Capability.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA -> Capability.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING -> Capability.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR -> Capability.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING -> Capability.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT -> Capability.REQUEST_AVAILABLE_CAPABILITIES_DYNAMIC_RANGE_TEN_BIT
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE -> Capability.REQUEST_AVAILABLE_CAPABILITIES_STREAM_USE_CASE
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES -> Capability.REQUEST_AVAILABLE_CAPABILITIES_COLOR_SPACE_PROFILES
        else -> Capability.REQUEST_AVAILABLE_CAPABILITIES_UNKNOWN
    }
}

fun cameraCharacteristicsToCapabilities(capabilityIds: IntArray?): List<Capability> {
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

