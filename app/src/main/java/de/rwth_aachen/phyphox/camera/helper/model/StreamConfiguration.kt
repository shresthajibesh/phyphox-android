package de.rwth_aachen.phyphox.camera.helper.model

import com.google.gson.annotations.SerializedName

data class StreamConfiguration(
    @SerializedName("format")
    val format: Int,

    @SerializedName("outputSizes")
    val outputSizes: List<Size>,

    @SerializedName("highspeed")
    val highspeed: List<HighSpeedConfiguration>
)
