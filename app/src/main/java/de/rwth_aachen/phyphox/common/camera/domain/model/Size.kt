package de.rwth_aachen.phyphox.common.camera.domain.model

import com.google.gson.annotations.SerializedName

data class Size(
    @SerializedName("w") val width: Int,
    @SerializedName("h") val height: Int,
)
