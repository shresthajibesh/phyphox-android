package de.rwth_aachen.phyphox.common.camera.domain.model

import com.google.gson.annotations.SerializedName

/*
* Minified version of CameraInfo
* */
data class CameraInfoSnippet(
    @SerializedName("id")
    val id: String,

    @SerializedName("lensFacing")
    val lensFacing: LensFacing,

    @SerializedName("hardwareLevel")
    val hardwareLevel: HardwareLevel,

    @SerializedName("capabilities")
    val capabilities: List<CameraCapability>,
)
