package de.rwth_aachen.phyphox.common.camera.data.converter

import android.os.Build
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GetPhysicalCameraIdListsTest {
    @Test
    fun `physicalCamIds returned on P and above`() {
        val result =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                setOf("0", "1")
            else
                emptySet()

        assertEquals(setOf("0", "1"), result)
    }

    @Config(sdk = [Build.VERSION_CODES.O_MR1])
    @Test
    fun `physicalCamIds empty below P`() {
        val result =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                setOf("0", "1")
            else
                emptySet()

        assertTrue(result.isEmpty())
    }

}



