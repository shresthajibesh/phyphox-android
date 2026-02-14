package de.rwth_aachen.phyphox.camera.helper

import android.hardware.camera2.CameraCharacteristics
import com.google.common.truth.Truth.assertThat
import de.rwth_aachen.phyphox.camera.model.CameraSettingMode
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CameraHelperTest {

    @Test
    fun should_convert_LensFacing_CameraCharacteristics_to_appropriate_String() {
        FacingConstToStringScenario.entries.forEach { scenario ->
            val result = CameraHelper.facingConstToString(scenario.givenCameraCharacteristics)
            assertEquals(scenario.expectedString, result)
        }
    }

    @Test
    fun should_convert_Supported_Hardware_Info_CameraCharacteristics_to_appropriate_String() {
        HardwareLevelToStringScenario.entries.forEach { scenario ->
            val result = CameraHelper.hardwareLevelConstToString(scenario.givenCameraCharacteristics)
            assertEquals(scenario.expectedString, result)
        }
    }

    @Test
    fun should_convert_Available_Capabilities_CameraCharacteristics_to_appropriate_String() {
        CapabilityToString.entries.forEach { scenario ->
            val result = CameraHelper.capabilityConstToString(scenario.givenCameraCharacteristics)
            assertEquals(scenario.expectedString, result)
        }
    }


    @Test
    fun should_convert_input_setting_to_setting_mode() {
        val input = "[iso, shutter speed, aperture]"
        val result = CameraHelper.convertInputSettingToSettingMode(input)
        assertThat(result).containsExactly(
            CameraSettingMode.ISO,
            CameraSettingMode.SHUTTER_SPEED,
            CameraSettingMode.APERTURE,
        ).inOrder()
    }

    @Test
    fun should_convert_nano_second_to_second() {
        val nanoseconds = 1000000000L
        val fraction = CameraHelper.convertNanoSecondToSecond(nanoseconds)
        assertThat(fraction.numerator).isEqualTo(1)
        assertThat(fraction.denominator).isEqualTo(1)
    }

    @Test
    fun testShutterSpeedRange() {
        val min = 1000000L
        val max = 1000000000L
        val range = CameraHelper.shutterSpeedRange(min, max)
        assertThat(range).isNotEmpty()
    }

    @Test
    fun testIsoRange() {
        val min = 100
        val max = 800
        val range = CameraHelper.isoRange(min, max)
        assertThat(range).containsExactly(100, 200, 400, 800).inOrder()
    }

    @Test
    fun testFindIsoNearestNumber() {
        val numbers = listOf(100, 200, 400, 800)
        val input = 350
        val result = CameraHelper.findIsoNearestNumber(input, numbers)
        assertThat(result).isEqualTo(400)
    }

    @Test
    fun testStringToNanoseconds() {
        val input = "1/1000"
        val result = CameraHelper.stringToNanoseconds(input)
        assertThat(result).isEqualTo(1000000)
    }

    @Test
    fun testExposureValueStringToFloat() {
        val input = "-0.7EV"
        val result = CameraHelper.exposureValueStringToFloat(input)
        assertThat(result).isEqualTo(-0.7f)
    }

    @Test
    fun testConvertTemperatureToRggb() {
        val temperature = 6600
        val result = CameraHelper.convertTemperatureToRggb(temperature)
        assertThat(result).hasLength(4)
    }

    @Suppress("unused")
    enum class FacingConstToStringScenario(
        val givenCameraCharacteristics: Int,
        val expectedString: String,
    ) {
        LENS_FACING_FRONT(
            CameraCharacteristics.LENS_FACING_FRONT,
            "LENS_FACING_FRONT",
        ),
        LENS_FACING_BACK(
            CameraCharacteristics.LENS_FACING_BACK,
            "LENS_FACING_BACK",
        ),
        LENS_FACING_EXTERNAL(
            CameraCharacteristics.LENS_FACING_EXTERNAL,
            "LENS_FACING_EXTERNAL",
        )
    }

    @Suppress("unused")
    enum class HardwareLevelToStringScenario(
        val givenCameraCharacteristics: Int,
        val expectedString: String,
    ) {
        INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED(
            givenCameraCharacteristics = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
            expectedString = "HARDWARE_LEVEL_LIMITED",
        ),
        INFO_SUPPORTED_HARDWARE_LEVEL_FULL(
            givenCameraCharacteristics = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
            expectedString = "HARDWARE_LEVEL_FULL",
        ),
        INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY(
            givenCameraCharacteristics = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
            expectedString = "HARDWARE_LEVEL_LEGACY",
        ),
        INFO_SUPPORTED_HARDWARE_LEVEL_3(
            givenCameraCharacteristics = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3,
            expectedString = "HARDWARE_LEVEL_3",
        ),
        INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL(
            givenCameraCharacteristics = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
            expectedString = "HARDWARE_LEVEL_EXTERNAL",
        )
    }

    @Suppress("unused")
    enum class CapabilityToString(
        val givenCameraCharacteristics: Int,
        val expectedString: String,
    ) {
        REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE,
            expectedString = "CAPABILITIES_BACKWARD_COMPATIBLE",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR,
            expectedString = "CAPABILITIES_MANUAL_SENSOR",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING,
            expectedString = "CAPABILITIES_MANUAL_POST_PROCESSING",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_RAW(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW,
            expectedString = "CAPABILITIES_RAW",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING,
            expectedString = "CAPABILITIES_PRIVATE_REPROCESSING",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS,
            expectedString = "CAPABILITIES_READ_SENSOR_SETTINGS",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE,
            expectedString = "CAPABILITIES_BURST_CAPTURE",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING,
            expectedString = "CAPABILITIES_YUV_REPROCESSING",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT,
            expectedString = "CAPABILITIES_DEPTH_OUTPUT",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO,
            expectedString = "CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING,
            expectedString = "CAPABILITIES_MOTION_TRACKING",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA,
            expectedString = "CAPABILITIES_LOGICAL_MULTI_CAMERA",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME,
            expectedString = "CAPABILITIES_MONOCHROME",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA,
            expectedString = "CAPABILITIES_SECURE_IMAGE_DATA",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_SYSTEM_CAMERA,
            expectedString = "CAPABILITIES_SYSTEM_CAMERA",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_OFFLINE_PROCESSING,
            expectedString = "CAPABILITIES_OFFLINE_PROCESSING",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR,
            expectedString = "CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR",
        ),
        REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING(
            givenCameraCharacteristics = CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_REMOSAIC_REPROCESSING,
            expectedString = "CAPABILITIES_REMOSAIC_REPROCESSING",
        ),
    }

}
