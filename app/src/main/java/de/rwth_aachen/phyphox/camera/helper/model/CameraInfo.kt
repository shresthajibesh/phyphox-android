package de.rwth_aachen.phyphox.camera.helper.model

import com.google.gson.annotations.SerializedName

typealias CameraInfoList = List<CameraInfo>
data class CameraInfo(
    @SerializedName("id")
    val id: String,

    @SerializedName("facing")
    val lensFacing: LensFacing,

    @SerializedName("hardwareLevel")
    val hardwareLevel: String,

    @SerializedName("capabilities")
    val capabilities: List<String>,

    @SerializedName("captureRequestKeys")
    val captureRequestKeys: List<String>,

    @SerializedName("captureResultKeys")
    val captureResultKeys: List<String>,

    @SerializedName("fpsRanges")
    val fpsRanges: List<FpsRange>,

    @SerializedName("physicalCamIds")
    val physicalCamIds: List<String>,

    @SerializedName("streamConfigurations")
    val streamConfigurations: List<StreamConfiguration>
)
