package de.rwth_aachen.phyphox.common.camera.domain.model.error

class CameraInfoNotFound(cameraId: String) : Throwable("Camera Info for camera with id $cameraId not found")
