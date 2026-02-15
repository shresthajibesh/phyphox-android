package de.rwth_aachen.phyphox.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.rwth_aachen.phyphox.common.camera.di.CommonCameraModule

@Module(
    includes = [
        CommonCameraModule::class,
    ],
)
@InstallIn(SingletonComponent::class)
abstract class AppModule {}

