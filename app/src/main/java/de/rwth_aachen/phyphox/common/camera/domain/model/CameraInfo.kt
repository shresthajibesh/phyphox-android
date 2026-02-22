package de.rwth_aachen.phyphox.common.camera.domain.model

import com.google.gson.annotations.SerializedName

typealias CameraInfoList = List<CameraInfo>

sealed interface CameraInfo {
    data class FullCameraInfo(
        @SerializedName("id")
        val id: String,

        @SerializedName("facing")
        val lensFacing: LensFacing,

        @SerializedName("hardwareLevel")
        val hardwareLevel: HardwareLevel,

        @SerializedName("capabilities")
        val capabilities: List<CameraCapability>,

        @SerializedName("captureRequestKeys")
        val captureRequestKeys: List<String>,

        @SerializedName("captureResultKeys")
        val captureResultKeys: List<String>,

        @SerializedName("fpsRanges")
        val fpsRanges: List<FpsRange>,

        @SerializedName("physicalCamIds")
        val physicalCamIds: List<String>,

        @SerializedName("streamConfigurations")
        val streamConfigurations: List<StreamConfiguration>,
    ) : CameraInfo

    data class PartialCameraInfo(
        @SerializedName("id")
        val id: String,

        @SerializedName("lensFacing")
        val lensFacing: LensFacing,

        @SerializedName("hardwareLevel")
        val hardwareLevel: HardwareLevel,

        @SerializedName("capabilities")
        val capabilities: List<CameraCapability>,
    ) : CameraInfo

}
