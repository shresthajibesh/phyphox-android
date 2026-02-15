package de.rwth_aachen.phyphox

import android.app.Application
import android.hardware.camera2.CameraManager
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import de.rwth_aachen.phyphox.camera.helper.CameraHelper

//This extension to application is only used to store measured data in memory as this may easily exceed the amount of data allowed on the transaction stack
@HiltAndroidApp
class App : Application() {

    //Need to get rid off of this ASAP
    @JvmField
    var experiment: PhyphoxExperiment? = null

    override fun onCreate() {
        super.onCreate()
        val cameraManager: CameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        val cc =cameraManager.getCameraCharacteristics(cameraManager.cameraIdList.first())
        CameraHelper.updateCameraList(cameraManager)
        Log.d("App", "Camera list updated $cc")
    }
}
