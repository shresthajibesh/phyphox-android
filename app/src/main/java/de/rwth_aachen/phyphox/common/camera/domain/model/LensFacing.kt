package de.rwth_aachen.phyphox.common.camera.domain.model

import com.google.gson.annotations.SerializedName

enum class LensFacing {
    @SerializedName("LENS_FACING_FRONT")
    LENS_FACING_FRONT,

    @SerializedName("LENS_FACING_BACK")
    LENS_FACING_BACK,

    @SerializedName("LENS_FACING_EXTERNAL")
    LENS_FACING_EXTERNAL,

    @SerializedName("UNKNOWN")
    UNKNOWN;
}
