package de.rwth_aachen.phyphox.testfixture

import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.params.StreamConfigurationMap
import android.util.Range
import android.util.Size
import io.mockk.every
import io.mockk.mockk

@Suppress("DEPRECATION")
object CameraManagerFixture {

    val CAPABILITIES = intArrayOf(
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR,
        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING,
    )

    val CAPTURE_REQUEST_KEYS = listOf(
        CaptureRequest.CONTROL_MODE,
        CaptureRequest.CONTROL_AF_MODE,
        CaptureRequest.CONTROL_AE_MODE,
        CaptureRequest.SENSOR_EXPOSURE_TIME,
        CaptureRequest.SENSOR_SENSITIVITY,
        CaptureRequest.SCALER_CROP_REGION,
    )

    val CAPTURE_RESULT_KEYS = listOf<CaptureResult.Key<*>>(
        CaptureResult.CONTROL_AF_STATE,
        CaptureResult.CONTROL_AE_STATE,
        CaptureResult.SENSOR_EXPOSURE_TIME,
        CaptureResult.SENSOR_SENSITIVITY,
        CaptureResult.SENSOR_TIMESTAMP,
    )

    val FPS_RANGE_1 = Range(1, 100)
    val FPS_RANGE_2 = Range(10, 10000)
    val FPS_RANGES = arrayOf(FPS_RANGE_1, FPS_RANGE_2)

    const val CAMERA_ID_1 = "1"
    const val CAMERA_ID_2 = "10"
    const val CAMERA_ID_3 = "100"
    val CAMERA_IDS = setOf(CAMERA_ID_1, CAMERA_ID_2, CAMERA_ID_3)

    const val SENSOR_INFO_EXPOSURE_TIME_RANGE_LOWER = 1_000L
    const val SENSOR_INFO_EXPOSURE_TIME_RANGE_UPPER = 1_000_000_000L
    const val SENSOR_ORIENTATION = 90
    val SENSOR_INFO_EXPOSURE_TIME_RANGE = Range(
        SENSOR_INFO_EXPOSURE_TIME_RANGE_LOWER,
        SENSOR_INFO_EXPOSURE_TIME_RANGE_UPPER,
    )

    val PIXEL_FORMATS = intArrayOf(
        PixelFormat.TRANSPARENT,
        PixelFormat.OPAQUE,
        PixelFormat.RGBA_8888,
        PixelFormat.RGBX_8888,
        PixelFormat.RGB_888,
        PixelFormat.RGB_565,
        PixelFormat.RGBA_5551,
        PixelFormat.RGBA_4444,
        PixelFormat.A_8,
        PixelFormat.L_8,
        PixelFormat.LA_88,
        PixelFormat.RGB_332,
        PixelFormat.YCbCr_422_SP,
        PixelFormat.YCbCr_420_SP,
        PixelFormat.YCbCr_422_I,
        PixelFormat.RGBA_F16,
        PixelFormat.RGBA_1010102,
        PixelFormat.JPEG,
    )
    val IMAGE_FORMATS = intArrayOf(
        ImageFormat.RGB_565,
        ImageFormat.YV12,
        ImageFormat.Y8,
        ImageFormat.YCBCR_P010,
        ImageFormat.YCBCR_P210,
        ImageFormat.NV16,
        ImageFormat.NV21,
        ImageFormat.YUY2,
        ImageFormat.JPEG,
        ImageFormat.DEPTH_JPEG,
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888,
        ImageFormat.FLEX_RGB_888,
        ImageFormat.FLEX_RGBA_8888,
        ImageFormat.RAW_SENSOR,
        ImageFormat.RAW_PRIVATE,
        ImageFormat.RAW10,
        ImageFormat.RAW12,
        ImageFormat.DEPTH16,
        ImageFormat.DEPTH_POINT_CLOUD,
        ImageFormat.PRIVATE,
        ImageFormat.HEIC,
        ImageFormat.HEIC_ULTRAHDR,
        ImageFormat.JPEG_R,
    )
    val FORMATS = IMAGE_FORMATS + PIXEL_FORMATS

    val OUTPUT_SIZE_1 = Size(1920, 1080)
    val OUTPUT_SIZE_2 = Size(1280, 720)

    val OUTPUT_SIZES = arrayOf(OUTPUT_SIZE_1, OUTPUT_SIZE_2)

    val HIGH_SPEED_VIDEO_SIZE_1 = Size(1920, 1080)
    val HIGH_SPEED_VIDEO_SIZE_2 = Size(1280, 720)
    val HIGH_SPEED_VIDEO_SIZES = arrayOf(HIGH_SPEED_VIDEO_SIZE_1, HIGH_SPEED_VIDEO_SIZE_2)

    val HIGH_SPEED_VIDEO_FPS_RANGE_1 = Range(10, 1000)
    val HIGH_SPEED_VIDEO_FPS_RANGE_2 = Range(1, 500)
    val HIGH_SPEED_VIDEO_FPS_RANGES = arrayOf(HIGH_SPEED_VIDEO_FPS_RANGE_1, HIGH_SPEED_VIDEO_FPS_RANGE_2)

    fun getMockCameraCharacteristics(
        facing: Int = CameraCharacteristics.LENS_FACING_FRONT,
        hardwareLevel: Int = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
        sensorOrientation: Int = SENSOR_ORIENTATION,
        sensorInfoExposureTimeRange: Range<Long> = SENSOR_INFO_EXPOSURE_TIME_RANGE,
        physicalCameraIdentifiers: Set<String> = CAMERA_IDS,
        fpsRanges: Array<Range<Int>> = FPS_RANGES,
        captureResultKeys: List<CaptureResult.Key<*>> = CAPTURE_RESULT_KEYS,
        captureRequestKeys: List<CaptureRequest.Key<*>> = CAPTURE_REQUEST_KEYS,
        capabilities: IntArray = CAPABILITIES,
        formats: IntArray = FORMATS,
        outputSizes: Array<Size> = OUTPUT_SIZES,
        highSpeedVideoSizes: Array<Size> = HIGH_SPEED_VIDEO_SIZES,
        highSpeedVideoFpsRanges: Array<Range<Int>> = HIGH_SPEED_VIDEO_FPS_RANGES,
    ): CameraCharacteristics {

        val mockStreamMap = mockk<StreamConfigurationMap> {
            every { getOutputSizes(any()) } returns outputSizes
            every { getHighSpeedVideoSizes() } returns highSpeedVideoSizes
            every { getHighSpeedVideoFpsRangesFor(any()) } returns highSpeedVideoFpsRanges
            every { outputFormats } returns formats
        }

        val map = mutableMapOf(
            CameraCharacteristics.LENS_FACING to facing,
            CameraCharacteristics.SENSOR_ORIENTATION to sensorOrientation,
            CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES to capabilities,
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL to hardwareLevel,
            CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE to sensorInfoExposureTimeRange,
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP to mockStreamMap,
            CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES to fpsRanges,
        )

        return mockk<CameraCharacteristics> {
            // 🔴 CRITICAL: generic fallback — prevents MockK auto-hinting
            every { get<Any>(any()) } answers {
                map[firstArg()]
            }
            every { getAvailableCaptureRequestKeys() } returns captureRequestKeys
            every { getAvailableCaptureResultKeys() } returns captureResultKeys
            every { this@mockk.physicalCameraIds } returns physicalCameraIdentifiers
        }
    }

    fun getMockCameraManager(
        cameraCharacteristicsMap: Map<String, CameraCharacteristics> = mapOf(
            CAMERA_ID_1 to getMockCameraCharacteristics(
                facing = CameraCharacteristics.LENS_FACING_FRONT,
            ),
            CAMERA_ID_2 to getMockCameraCharacteristics(
                facing = CameraCharacteristics.LENS_FACING_BACK,
            ),
            CAMERA_ID_3 to getMockCameraCharacteristics(
                facing = CameraCharacteristics.LENS_FACING_EXTERNAL,
            ),
        ),
    ): CameraManager {
        return mockk<CameraManager>(relaxed = true) {
            every { cameraIdList } returns cameraCharacteristicsMap.keys.toTypedArray()
            cameraCharacteristicsMap.forEach { (cameraId, cameraCharacteristics) ->
                every { getCameraCharacteristics(cameraId) } returns cameraCharacteristics
            }
        }
    }
}
