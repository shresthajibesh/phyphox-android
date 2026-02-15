package de.rwth_aachen.phyphox.camera.helper.model

import com.google.gson.annotations.SerializedName

enum class LensFacing(val value: String) {
    @SerializedName("LENS_FACING_FRONT")
    FRONT("LENS_FACING_FRONT"),

    @SerializedName("LENS_FACING_BACK")
    BACK("LENS_FACING_BACK"),

    @SerializedName("LENS_FACING_EXTERNAL")
    EXTERNAL("LENS_FACING_EXTERNAL"),

    @SerializedName("UNKNOWN")
    UNKNOWN("UNKNOWN")
}
