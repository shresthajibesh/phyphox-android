package de.rwth_aachen.phyphox.camera.helper.model

import com.google.gson.annotations.SerializedName

data class HighSpeedConfiguration(
    @SerializedName("w")
    val width: Int,

    @SerializedName("h")
    val height: Int,

    @SerializedName("fpsRanges")
    val fpsRanges: List<FpsRange>
)
