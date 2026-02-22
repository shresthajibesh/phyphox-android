package de.rwth_aachen.phyphox.common.camera.data.converter

import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import de.rwth_aachen.phyphox.common.camera.domain.model.CameraInfo
import de.rwth_aachen.phyphox.common.camera.domain.model.Capability
import de.rwth_aachen.phyphox.common.camera.domain.model.FpsRange
import de.rwth_aachen.phyphox.common.camera.domain.model.HardwareLevel
import de.rwth_aachen.phyphox.common.camera.domain.model.HighSpeedVideoSizeConfiguration
import de.rwth_aachen.phyphox.common.camera.domain.model.LensFacing
import de.rwth_aachen.phyphox.common.camera.domain.model.Size
import de.rwth_aachen.phyphox.common.camera.domain.model.StreamConfiguration
import de.rwth_aachen.phyphox.testfixture.CameraManagerFixture
import io.kotest.matchers.equals.shouldBeEqual
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CameraCharacteristicsToCameraInfoConverterTest {


    @Test
    fun convertsSuccessfully() {
        val converter = CameraCharacteristicsToCameraInfoConverter()
        val result = converter.convert(
            "1",
            CameraManagerFixture.getMockCameraCharacteristics(
                CameraCharacteristics.LENS_FACING_FRONT,
            ),
        )
        result shouldBeEqual CameraInfo(
            id = "1",
            lensFacing = LensFacing.LENS_FACING_FRONT,
            hardwareLevel = HardwareLevel.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
            capabilities = listOf(
                Capability.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_RAW,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR,
                Capability.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING,

                ),
            captureRequestKeys = listOf(
                "android.control.mode",
                "android.control.afMode",
                "android.control.aeMode",
                "android.sensor.exposureTime",
                "android.sensor.sensitivity",
                "android.scaler.cropRegion",
            ),
            captureResultKeys = listOf(
                "android.control.afState",
                "android.control.aeState",
                "android.sensor.exposureTime",
                "android.sensor.sensitivity",
                "android.sensor.timestamp",
            ),
            fpsRanges = listOf(
                FpsRange(min = 1, max = 100), FpsRange(min = 10, max = 10000),
            ),
            physicalCamIds = listOf("1", "10", "100"),
            streamConfigurations = listOf(
                StreamConfiguration(
                    format = 4,
                    outputSizes = listOf(
                        Size(width = 1920, height = 1080), Size(width = 1280, height = 720),
                    ),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(
                                FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500),
                            ),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(
                                FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500),
                            ),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 842094169,
                    outputSizes = listOf(
                        Size(width = 1920, height = 1080), Size(width = 1280, height = 720)
                    ),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(
                                FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)
                            ),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(
                                FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)
                            ),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 538982489,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 54,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 60,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 16,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 17,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 20,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 256,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 1768253795,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 35,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 39,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 40,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 41,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 42,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 32,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 36,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 37,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 38,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 1144402265,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 257,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 34,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 1212500294,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 4102,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 4101,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = -2,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = -1,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 1,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 2,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 3,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 4,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 6,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 7,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 8,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 9,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 10,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 11,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 16,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 17,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 20,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 22,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 43,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
                StreamConfiguration(
                    format = 256,
                    outputSizes = listOf(Size(width = 1920, height = 1080), Size(width = 1280, height = 720)),
                    highSpeedVideoSize = listOf(
                        HighSpeedVideoSizeConfiguration(
                            width = 1920,
                            height = 1080,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                        HighSpeedVideoSizeConfiguration(
                            width = 1280,
                            height = 720,
                            fpsRanges = listOf(FpsRange(min = 10, max = 1000), FpsRange(min = 1, max = 500)),
                        ),
                    ),
                ),
            ),
        )
    }
}
