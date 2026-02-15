package de.rwth_aachen.phyphox.camera.helper.model

import com.google.gson.annotations.SerializedName

data class FpsRange(
    @SerializedName("min")
    val min: Int,

    @SerializedName("max")
    val max: Int
)
