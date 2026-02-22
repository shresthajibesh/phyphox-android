package de.rwth_aachen.phyphox.common.camera.domain.model

import com.google.gson.annotations.SerializedName

enum class HardwareLevel {
    @SerializedName("HARDWARE_LEVEL_LIMITED")
    INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
    @SerializedName("HARDWARE_LEVEL_FULL")
    INFO_SUPPORTED_HARDWARE_LEVEL_FULL,
    @SerializedName("HARDWARE_LEVEL_LEGACY")
    INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
    @SerializedName("HARDWARE_LEVEL_3")
    INFO_SUPPORTED_HARDWARE_LEVEL_3,
    @SerializedName("HARDWARE_LEVEL_EXTERNAL")
    INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL,
    @SerializedName("UNKNOWN")
    UNKNOWN

}
