package de.rwth_aachen.phyphox.common.camera.domain.model

import com.google.gson.annotations.SerializedName

data class StreamConfiguration(
    @SerializedName("format") val format: Int,

    @SerializedName("outputSizes") val outputSizes: List<Size>,

    @SerializedName("highspeed") val highSpeedVideoSize: List<HighSpeedVideoSizeConfiguration>,
)
