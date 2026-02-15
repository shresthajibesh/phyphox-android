package de.rwth_aachen.phyphox.camera.helper.model

import com.google.gson.annotations.SerializedName

data class Size(
    @SerializedName("w")
    val width: Int,

    @SerializedName("h")
    val height: Int
)
