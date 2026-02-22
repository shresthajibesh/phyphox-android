package de.rwth_aachen.phyphox.testfixture

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import com.google.common.truth.Truth
import com.google.gson.GsonBuilder
import de.rwth_aachen.phyphox.camera.helper.CameraHelper
import de.rwth_aachen.phyphox.camera.model.CameraSettingMode
import de.rwth_aachen.phyphox.common.camera.data.converter.CameraCharacteristicsToCameraInfoConverter
import io.kotest.matchers.equals.shouldBeEqual
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CameraHelperTest {

    @Test
    fun should_convert_LensFacing_CameraCharacteristics_to_appropriate_String() {
        FacingConstToStringScenario.entries.forEach { scenario ->
            val result = CameraHelper.facingConstToString(scenario.givenCameraCharacteristics)
            TestCase.assertEquals(scenario.expectedString, result)
        }
    }

    @Test
    fun should_convert_Supported_Hardware_Info_CameraCharacteristics_to_appropriate_String() {
        HardwareLevelToStringScenario.entries.forEach { scenario ->
            val result = CameraHelper.hardwareLevelConstToString(scenario.givenCameraCharacteristics)
            TestCase.assertEquals(scenario.expectedString, result)
        }
    }

    @Test
    fun should_convert_Available_Capabilities_CameraCharacteristics_to_appropriate_String() {
        CapabilityToString.entries.forEach { scenario ->
            val result = CameraHelper.capabilityConstToString(scenario.givenCameraCharacteristics)
            TestCase.assertEquals(scenario.expectedString, result)
        }
    }


    @Test
    fun should_convert_input_setting_to_setting_mode() {
        val input = "[iso, shutter speed, aperture]"
        val result = CameraHelper.convertInputSettingToSettingMode(input)
        Truth.assertThat(result).containsExactly(
            CameraSettingMode.ISO,
            CameraSettingMode.SHUTTER_SPEED,
            CameraSettingMode.APERTURE,
        ).inOrder()
    }

    @Test
    fun should_convert_nano_second_to_second() {
        val nanoseconds = 1000000000L
        val fraction = CameraHelper.convertNanoSecondToSecond(nanoseconds)
        Truth.assertThat(fraction.numerator).isEqualTo(1)
        Truth.assertThat(fraction.denominator).isEqualTo(1)
    }

    @Test
    fun testShutterSpeedRange() {
        val min = 1000000L
        val max = 1000000000L
        val range = CameraHelper.shutterSpeedRange(min, max)
        Truth.assertThat(range).isNotEmpty()
    }

    @Test
    fun testIsoRange() {
        val min = 100
        val max = 800
        val range = CameraHelper.isoRange(min, max)
        Truth.assertThat(range).containsExactly(100, 200, 400, 800).inOrder()
    }

    @Test
    fun testFindIsoNearestNumber() {
        val numbers = listOf(100, 200, 400, 800)
        val input = 350
        val result = CameraHelper.findIsoNearestNumber(input, numbers)
        Truth.assertThat(result).isEqualTo(400)
    }

    @Test
    fun testStringToNanoseconds() {
        val input = "1/1000"
        val result = CameraHelper.stringToNanoseconds(input)
        Truth.assertThat(result).isEqualTo(1000000)
    }

    @Test
    fun testExposureValueStringToFloat() {
        val input = "-0.7EV"
        val result = CameraHelper.exposureValueStringToFloat(input)
        Truth.assertThat(result).isEqualTo(-0.7f)
    }

    @Test
    fun testConvertTemperatureToRggb() {
        val temperature = 6600
        val result = CameraHelper.convertTemperatureToRggb(temperature)
        Truth.assertThat(result).hasLength(4)
    }

    @Test
    fun testGetCamera2FormattedCapsPartial() {
        val cameraManager = CameraManagerFixture.getMockCameraManager()
        val expectedResult =
            "[{\"id\":\"1\",\"facing\":\"LENS_FACING_FRONT\",\"hardwareLevel\":\"HARDWARE_LEVEL_FULL\",\"capabilities\":[\"CAPABILITIES_MANUAL_SENSOR\",\"CAPABILITIES_BURST_CAPTURE\",\"CAPABILITIES_BACKWARD_COMPATIBLE\",\"CAPABILITIES_RAW\",\"CAPABILITIES_PRIVATE_REPROCESSING\",\"CAPABILITIES_READ_SENSOR_SETTINGS\",\"CAPABILITIES_YUV_REPROCESSING\",\"CAPABILITIES_DEPTH_OUTPUT\",\"CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO\",\"CAPABILITIES_MOTION_TRACKING\",\"CAPABILITIES_LOGICAL_MULTI_CAMERA\",\"CAPABILITIES_MONOCHROME\",\"CAPABILITIES_SECURE_IMAGE_DATA\",\"CAPABILITIES_SYSTEM_CAMERA\",\"CAPABILITIES_OFFLINE_PROCESSING\",\"CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR\",\"CAPABILITIES_REMOSAIC_REPROCESSING\"]},{\"id\":\"100\",\"facing\":\"LENS_FACING_EXTERNAL\",\"hardwareLevel\":\"HARDWARE_LEVEL_FULL\",\"capabilities\":[\"CAPABILITIES_MANUAL_SENSOR\",\"CAPABILITIES_BURST_CAPTURE\",\"CAPABILITIES_BACKWARD_COMPATIBLE\",\"CAPABILITIES_RAW\",\"CAPABILITIES_PRIVATE_REPROCESSING\",\"CAPABILITIES_READ_SENSOR_SETTINGS\",\"CAPABILITIES_YUV_REPROCESSING\",\"CAPABILITIES_DEPTH_OUTPUT\",\"CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO\",\"CAPABILITIES_MOTION_TRACKING\",\"CAPABILITIES_LOGICAL_MULTI_CAMERA\",\"CAPABILITIES_MONOCHROME\",\"CAPABILITIES_SECURE_IMAGE_DATA\",\"CAPABILITIES_SYSTEM_CAMERA\",\"CAPABILITIES_OFFLINE_PROCESSING\",\"CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR\",\"CAPABILITIES_REMOSAIC_REPROCESSING\"]},{\"id\":\"10\",\"facing\":\"LENS_FACING_BACK\",\"hardwareLevel\":\"HARDWARE_LEVEL_FULL\",\"capabilities\":[\"CAPABILITIES_MANUAL_SENSOR\",\"CAPABILITIES_BURST_CAPTURE\",\"CAPABILITIES_BACKWARD_COMPATIBLE\",\"CAPABILITIES_RAW\",\"CAPABILITIES_PRIVATE_REPROCESSING\",\"CAPABILITIES_READ_SENSOR_SETTINGS\",\"CAPABILITIES_YUV_REPROCESSING\",\"CAPABILITIES_DEPTH_OUTPUT\",\"CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO\",\"CAPABILITIES_MOTION_TRACKING\",\"CAPABILITIES_LOGICAL_MULTI_CAMERA\",\"CAPABILITIES_MONOCHROME\",\"CAPABILITIES_SECURE_IMAGE_DATA\",\"CAPABILITIES_SYSTEM_CAMERA\",\"CAPABILITIES_OFFLINE_PROCESSING\",\"CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR\",\"CAPABILITIES_REMOSAIC_REPROCESSING\"]}]"

        CameraHelper.updateCameraList(cameraManager)
        val result = CameraHelper.getCamera2FormattedCaps(false)
        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun testGetCamera2FormattedCapsFull() {
        val cameraManager = CameraManagerFixture.getMockCameraManager()
        val expectedResult =
            "[{\"id\":\"1\",\"facing\":\"LENS_FACING_FRONT\",\"hardwareLevel\":\"HARDWARE_LEVEL_FULL\",\"capabilities\":[\"CAPABILITIES_MANUAL_SENSOR\",\"CAPABILITIES_BURST_CAPTURE\",\"CAPABILITIES_BACKWARD_COMPATIBLE\",\"CAPABILITIES_RAW\",\"CAPABILITIES_PRIVATE_REPROCESSING\",\"CAPABILITIES_READ_SENSOR_SETTINGS\",\"CAPABILITIES_YUV_REPROCESSING\",\"CAPABILITIES_DEPTH_OUTPUT\",\"CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO\",\"CAPABILITIES_MOTION_TRACKING\",\"CAPABILITIES_LOGICAL_MULTI_CAMERA\",\"CAPABILITIES_MONOCHROME\",\"CAPABILITIES_SECURE_IMAGE_DATA\",\"CAPABILITIES_SYSTEM_CAMERA\",\"CAPABILITIES_OFFLINE_PROCESSING\",\"CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR\",\"CAPABILITIES_REMOSAIC_REPROCESSING\"],\"captureRequestKeys\":[\"android.control.mode\",\"android.control.afMode\",\"android.control.aeMode\",\"android.sensor.exposureTime\",\"android.sensor.sensitivity\",\"android.scaler.cropRegion\"],\"captureResultKeys\":[\"android.control.afState\",\"android.control.aeState\",\"android.sensor.exposureTime\",\"android.sensor.sensitivity\",\"android.sensor.timestamp\"],\"fpsRanges\":[{\"min\":1,\"max\":100},{\"min\":10,\"max\":10000}],\"physicalCamIds\":[\"1\",\"10\",\"100\"],\"streamConfigurations\":[{\"format\":4,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":842094169,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":538982489,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":54,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":60,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":16,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":17,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":20,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":256,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1768253795,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":35,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":39,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":40,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":41,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":42,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":32,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":36,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":37,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":38,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1144402265,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":257,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":34,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1212500294,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4102,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4101,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":-2,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":-1,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":2,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":3,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":6,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":7,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":8,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":9,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":10,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":11,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":16,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":17,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":20,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":22,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":43,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":256,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]}]},{\"id\":\"100\",\"facing\":\"LENS_FACING_EXTERNAL\",\"hardwareLevel\":\"HARDWARE_LEVEL_FULL\",\"capabilities\":[\"CAPABILITIES_MANUAL_SENSOR\",\"CAPABILITIES_BURST_CAPTURE\",\"CAPABILITIES_BACKWARD_COMPATIBLE\",\"CAPABILITIES_RAW\",\"CAPABILITIES_PRIVATE_REPROCESSING\",\"CAPABILITIES_READ_SENSOR_SETTINGS\",\"CAPABILITIES_YUV_REPROCESSING\",\"CAPABILITIES_DEPTH_OUTPUT\",\"CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO\",\"CAPABILITIES_MOTION_TRACKING\",\"CAPABILITIES_LOGICAL_MULTI_CAMERA\",\"CAPABILITIES_MONOCHROME\",\"CAPABILITIES_SECURE_IMAGE_DATA\",\"CAPABILITIES_SYSTEM_CAMERA\",\"CAPABILITIES_OFFLINE_PROCESSING\",\"CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR\",\"CAPABILITIES_REMOSAIC_REPROCESSING\"],\"captureRequestKeys\":[\"android.control.mode\",\"android.control.afMode\",\"android.control.aeMode\",\"android.sensor.exposureTime\",\"android.sensor.sensitivity\",\"android.scaler.cropRegion\"],\"captureResultKeys\":[\"android.control.afState\",\"android.control.aeState\",\"android.sensor.exposureTime\",\"android.sensor.sensitivity\",\"android.sensor.timestamp\"],\"fpsRanges\":[{\"min\":1,\"max\":100},{\"min\":10,\"max\":10000}],\"physicalCamIds\":[\"1\",\"10\",\"100\"],\"streamConfigurations\":[{\"format\":4,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":842094169,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":538982489,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":54,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":60,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":16,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":17,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":20,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":256,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1768253795,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":35,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":39,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":40,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":41,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":42,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":32,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":36,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":37,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":38,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1144402265,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":257,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":34,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1212500294,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4102,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4101,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":-2,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":-1,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":2,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":3,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":6,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":7,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":8,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":9,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":10,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":11,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":16,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":17,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":20,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":22,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":43,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":256,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]}]},{\"id\":\"10\",\"facing\":\"LENS_FACING_BACK\",\"hardwareLevel\":\"HARDWARE_LEVEL_FULL\",\"capabilities\":[\"CAPABILITIES_MANUAL_SENSOR\",\"CAPABILITIES_BURST_CAPTURE\",\"CAPABILITIES_BACKWARD_COMPATIBLE\",\"CAPABILITIES_RAW\",\"CAPABILITIES_PRIVATE_REPROCESSING\",\"CAPABILITIES_READ_SENSOR_SETTINGS\",\"CAPABILITIES_YUV_REPROCESSING\",\"CAPABILITIES_DEPTH_OUTPUT\",\"CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO\",\"CAPABILITIES_MOTION_TRACKING\",\"CAPABILITIES_LOGICAL_MULTI_CAMERA\",\"CAPABILITIES_MONOCHROME\",\"CAPABILITIES_SECURE_IMAGE_DATA\",\"CAPABILITIES_SYSTEM_CAMERA\",\"CAPABILITIES_OFFLINE_PROCESSING\",\"CAPABILITIES_ULTRA_HIGH_RESOLUTION_SENSOR\",\"CAPABILITIES_REMOSAIC_REPROCESSING\"],\"captureRequestKeys\":[\"android.control.mode\",\"android.control.afMode\",\"android.control.aeMode\",\"android.sensor.exposureTime\",\"android.sensor.sensitivity\",\"android.scaler.cropRegion\"],\"captureResultKeys\":[\"android.control.afState\",\"android.control.aeState\",\"android.sensor.exposureTime\",\"android.sensor.sensitivity\",\"android.sensor.timestamp\"],\"fpsRanges\":[{\"min\":1,\"max\":100},{\"min\":10,\"max\":10000}],\"physicalCamIds\":[\"1\",\"10\",\"100\"],\"streamConfigurations\":[{\"format\":4,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":842094169,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":538982489,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":54,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":60,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":16,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":17,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":20,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":256,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1768253795,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":35,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":39,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":40,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":41,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":42,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":32,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":36,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":37,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":38,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1144402265,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":257,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":34,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1212500294,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4102,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4101,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":-2,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":-1,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":1,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":2,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":3,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":4,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":6,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":7,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":8,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":9,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":10,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":11,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":16,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":17,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":20,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":22,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":43,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]},{\"format\":256,\"outputSizes\":[{\"w\":1920,\"h\":1080},{\"w\":1280,\"h\":720}],\"highspeed\":[{\"w\":1920,\"h\":1080,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]},{\"w\":1280,\"h\":720,\"fpsRanges\":[{\"min\":10,\"max\":1000},{\"min\":1,\"max\":500}]}]}]}]"

        CameraHelper.updateCameraList(cameraManager)
        val result = CameraHelper.getCamera2FormattedCaps(true)
        result shouldBeEqual expectedResult

    }

    @Test
    fun testNewConverterResultsAreEquivalent() {
        val converter = CameraCharacteristicsToCameraInfoConverter()
        val cameraManager = CameraManagerFixture.getMockCameraManager()
        val infos = listOf(
            converter.convert("1", cameraManager.getCameraCharacteristics("1")),
            converter.convert("100", cameraManager.getCameraCharacteristics("100")),
            converter.convert("10", cameraManager.getCameraCharacteristics("10")),
        )
        val new = GsonBuilder().create().toJson(infos)

        CameraHelper.updateCameraList(cameraManager)
        val old = CameraHelper.getCamera2FormattedCaps(true)

        new shouldBeEqual old
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
