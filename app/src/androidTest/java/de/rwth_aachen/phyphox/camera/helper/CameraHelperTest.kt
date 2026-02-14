
package de.rwth_aachen.phyphox.camera.helper

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.rwth_aachen.phyphox.camera.model.CameraSettingMode
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraHelperTest {

    @Test
    fun testConvertInputSettingToSettingMode() {
        val input = "[iso, shutter speed, aperture]"
        val result = CameraHelper.convertInputSettingToSettingMode(input)
        assertThat(result).containsExactly(
            CameraSettingMode.ISO,
            CameraSettingMode.SHUTTER_SPEED,
            CameraSettingMode.APERTURE
        ).inOrder()
    }

    @Test
    fun testConvertNanoSecondToSecond() {
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
}
